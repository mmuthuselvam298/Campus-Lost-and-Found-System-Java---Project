package com.campusfind.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalStorageService.class);
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/webp", "image/jpg"
    );
    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024; // 15MB

    private final Path rootLocation;

    public LocalStorageService(@Value("${campusfind.storage.upload-dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
            Files.createDirectories(this.rootLocation.resolve("thumbnails"));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload storage location: " + this.rootLocation, e);
        }
    }

    @Override
    public StorageResult store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 15MB limit");
        }

        String rawContentType = file.getContentType();
        if (rawContentType == null || !ALLOWED_CONTENT_TYPES.contains(rawContentType.toLowerCase())) {
            throw new IllegalArgumentException("Unsupported file type: " + rawContentType + ". Allowed formats: JPEG, PNG, WEBP");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg");
        // Prevent path traversal
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            originalFilename = "safe_" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        }

        String extension = getFileExtension(originalFilename);
        if (extension.isEmpty() || (!extension.equals("png") && !extension.equals("webp") && !extension.equals("jpeg") && !extension.equals("jpg"))) {
            extension = "jpg";
        }

        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;
        String thumbnailFilename = "thumb_" + uniqueFilename;

        Path targetLocation = this.rootLocation.resolve(uniqueFilename).normalize();
        Path thumbnailLocation = this.rootLocation.resolve("thumbnails").resolve(thumbnailFilename).normalize();

        // Enforce boundary check
        if (!targetLocation.startsWith(this.rootLocation) || !thumbnailLocation.startsWith(this.rootLocation)) {
            throw new SecurityException("Target location outside designated storage root");
        }

        try {
            // Read image into memory to strip EXIF and validate actual raster integrity
            try (InputStream inputStream = file.getInputStream()) {
                BufferedImage originalImage = ImageIO.read(inputStream);
                if (originalImage == null) {
                    throw new IllegalArgumentException("Invalid image file format or corrupted content");
                }

                // Save sanitized full-size image (re-rendered to strip malicious EXIF/metadata)
                String formatName = extension.equals("png") ? "png" : "jpg";
                BufferedImage sanitizedImage = ensureRgbFormat(originalImage);
                ImageIO.write(sanitizedImage, formatName, targetLocation.toFile());

                // Generate thumbnail (max 400x400 preserving aspect ratio)
                BufferedImage thumbnail = createThumbnail(sanitizedImage, 400, 400);
                ImageIO.write(thumbnail, formatName, thumbnailLocation.toFile());
            }

            String fileUrl = "/uploads/" + uniqueFilename;
            String thumbnailUrl = "/uploads/thumbnails/" + thumbnailFilename;

            return new StorageResult(fileUrl, thumbnailUrl, targetLocation.toString());
        } catch (IOException e) {
            logger.error("Failed to store image file", e);
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public Path load(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be blank");
        }
        String cleanName = StringUtils.cleanPath(filename);
        if (cleanName.contains("..")) {
            throw new SecurityException("Path traversal attempt detected in filename: " + filename);
        }
        Path resolved = rootLocation.resolve(cleanName).normalize();
        if (!resolved.startsWith(rootLocation)) {
            throw new SecurityException("Access denied: File outside storage directory");
        }
        return resolved;
    }

    @Override
    public void delete(String filename) {
        try {
            Path file = load(filename);
            Files.deleteIfExists(file);
        } catch (Exception e) {
            logger.warn("Could not delete file {}: {}", filename, e.getMessage());
        }
    }

    private BufferedImage ensureRgbFormat(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_INT_RGB || src.getType() == BufferedImage.TYPE_INT_ARGB) {
            return src;
        }
        BufferedImage copy = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(src, 0, 0, null);
        g2d.dispose();
        return copy;
    }

    private BufferedImage createThumbnail(BufferedImage original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();

        double ratio = Math.min((double) maxWidth / width, (double) maxHeight / height);
        if (ratio >= 1.0) {
            return original;
        }

        int targetWidth = Math.max(1, (int) (width * ratio));
        int targetHeight = Math.max(1, (int) (height * ratio));

        BufferedImage thumbnail = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = thumbnail.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        return thumbnail;
    }

    private String getFileExtension(String filename) {
        int lastIndex = filename.lastIndexOf('.');
        if (lastIndex == -1) {
            return "";
        }
        return filename.substring(lastIndex + 1).toLowerCase();
    }
}

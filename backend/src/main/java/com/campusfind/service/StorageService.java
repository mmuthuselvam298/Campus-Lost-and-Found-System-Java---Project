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
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/webp", "image/jpg"
    );

    private final Path rootLocation;

    public StorageService(@Value("${campusfind.storage.upload-dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
            Files.createDirectories(this.rootLocation.resolve("thumbnails"));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload storage location", e);
        }
    }

    public StorageResult store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Unsupported file type: " + contentType + ". Allowed: JPEG, PNG, WEBP");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg");
        String extension = getFileExtension(originalFilename);
        if (extension.isEmpty()) {
            extension = "jpg";
        }

        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;
        String thumbnailFilename = "thumb_" + uniqueFilename;

        Path targetLocation = this.rootLocation.resolve(uniqueFilename);
        Path thumbnailLocation = this.rootLocation.resolve("thumbnails").resolve(thumbnailFilename);

        try {
            // Read image into memory to strip EXIF and validate actual image integrity
            try (InputStream inputStream = file.getInputStream()) {
                BufferedImage originalImage = ImageIO.read(inputStream);
                if (originalImage == null) {
                    throw new IllegalArgumentException("Invalid image file format or corrupted content");
                }

                // Save sanitized full-size image (re-rendered to strip malicious EXIF/metadata)
                ImageIO.write(originalImage, extension.equals("png") ? "png" : "jpg", targetLocation.toFile());

                // Generate thumbnail (max 400x400 preserving aspect ratio)
                BufferedImage thumbnail = createThumbnail(originalImage, 400, 400);
                ImageIO.write(thumbnail, extension.equals("png") ? "png" : "jpg", thumbnailLocation.toFile());
            }

            String fileUrl = "/uploads/" + uniqueFilename;
            String thumbnailUrl = "/uploads/thumbnails/" + thumbnailFilename;

            return new StorageResult(fileUrl, thumbnailUrl, targetLocation.toString());
        } catch (IOException e) {
            logger.error("Failed to store image file", e);
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    private BufferedImage createThumbnail(BufferedImage original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();

        double ratio = Math.min((double) maxWidth / width, (double) maxHeight / height);
        if (ratio >= 1.0) {
            return original;
        }

        int targetWidth = (int) (width * ratio);
        int targetHeight = (int) (height * ratio);

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

    public static class StorageResult {
        private final String fileUrl;
        private final String thumbnailUrl;
        private final String localFilePath;

        public StorageResult(String fileUrl, String thumbnailUrl, String localFilePath) {
            this.fileUrl = fileUrl;
            this.thumbnailUrl = thumbnailUrl;
            this.localFilePath = localFilePath;
        }

        public String getFileUrl() { return fileUrl; }
        public String getThumbnailUrl() { return thumbnailUrl; }
        public String getLocalFilePath() { return localFilePath; }
    }
}

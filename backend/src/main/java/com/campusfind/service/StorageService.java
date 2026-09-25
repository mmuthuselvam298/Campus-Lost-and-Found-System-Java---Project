package com.campusfind.service;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

/**
 * Storage service abstraction allowing local filesystem or cloud object stores (S3, MinIO, Supabase).
 */
public interface StorageService {

    StorageResult store(MultipartFile file);

    Path load(String filename);

    void delete(String filename);

    class StorageResult {
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

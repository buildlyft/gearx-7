package com.gearx7.app.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Uploads the file to storage and returns a stable key (S3 key or path).
     */

    String store(MultipartFile multipartFile);
}

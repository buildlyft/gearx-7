package com.gearx7.app.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentStorageService {
    String uploadMachineDocument(MultipartFile file, Long machineId);

    String uploadOperatorLicense(MultipartFile file, Long operatorId);

    String uploadOperatorPhoto(MultipartFile file, Long operatorId);

    String uploadCategoryImage(MultipartFile file, Long categoryId);

    String uploadSubcategoryImage(MultipartFile file, Long subcategoryId);

    String uploadTypeImage(MultipartFile file, Long typeId);

    void deleteByUrl(String url);

    void deleteOperatorFolder(Long operatorId);
}

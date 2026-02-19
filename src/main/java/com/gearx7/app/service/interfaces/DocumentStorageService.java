package com.gearx7.app.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentStorageService {
    String uploadMachineDocument(MultipartFile file, Long machineId);

    String uploadOperatorDocument(MultipartFile file, Long operatorId);

    String uploadCategoryImage(MultipartFile file, Long categoryId);

    String uploadSubcategoryImage(MultipartFile file, Long subcategoryId);

    String uploadTypeImage(MultipartFile file, Long typeId);
}

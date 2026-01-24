package com.gearx7.app.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentStorageService {
    String uploadMachineDocument(MultipartFile file, Long machineId);

    String uploadOperatorDocument(MultipartFile file, Long operatorId);
}

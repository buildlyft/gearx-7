package com.gearx7.app.service.mapper;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gearx7.app.service.FileStorageService;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryFileStorageService implements FileStorageService {

    private final Logger log = LoggerFactory.getLogger(CloudinaryFileStorageService.class);

    private final Cloudinary cloudinary;

    public CloudinaryFileStorageService(Cloudinary cloudinary) {
        log.info("STORAGE: CloudinaryFileStorageService initialized");
        this.cloudinary = cloudinary;
    }

    @Override
    public String store(MultipartFile multipartFile) {
        log.info("STORAGE: Uploading file to Cloudinary | name={} size={}", multipartFile.getOriginalFilename(), multipartFile.getSize());
        try {
            Map uploadResult = cloudinary
                .uploader()
                .upload(
                    multipartFile.getBytes(), // supports pdf, image, video etc
                    ObjectUtils.asMap("resource_type", "auto", "folder", "gearx7/machines/document")
                ); // "resource_type", "auto" Cloudinary automatically detects file type:
            // Image → stores in image bucket , Video → stores in video bucket , Raw files (pdf, docx etc) → stores in raw bucket , So you don’t need to manually specify.
            // You can store:
            // public_id  => stable cloudinary key
            // secure_url => https url

            String publicId = uploadResult.get("secure_url").toString();
            log.info("STORAGE SUCCESS: File uploaded | publicId={}", publicId);

            return publicId;
        } catch (IOException e) {
            log.error("STORAGE ERROR: Cloudinary upload failed | reason={}", e.getMessage(), e);
            throw new BadRequestAlertException("Failed to upload file to Cloudinary", "vehicleDocument", "cloudinaryUploadFailed");
        }
    }
}

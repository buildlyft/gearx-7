package com.gearx7.app.service.interfacesImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gearx7.app.service.interfaces.DocumentStorageService;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryDocumentStorageServiceImpl implements DocumentStorageService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryDocumentStorageServiceImpl.class);

    private final Cloudinary cloudinary;

    public CloudinaryDocumentStorageServiceImpl(Cloudinary cloudinary) {
        log.info("STORAGE initialized: CloudinaryDocumentStorageService");
        this.cloudinary = cloudinary;
    }

    /* ================= MACHINE DOCUMENTS ================= */

    @Override
    public String uploadMachineDocument(MultipartFile file, Long machineId) {
        String folder = "gearx-7/machines/machine-" + machineId + "/documents";

        return upload(file, folder, "MACHINE", machineId);
    }

    /* ================= OPERATOR DOCUMENTS ================= */

    @Override
    public String uploadOperatorDocument(MultipartFile file, Long operatorId) {
        String folder = "gearx-7/operators/operator-" + operatorId + "/documents";

        return upload(file, folder, "OPERATOR", operatorId);
    }

    /* ================= COMMON UPLOAD LOGIC ================= */

    private String upload(MultipartFile file, String folder, String type, Long ownerId) {
        try {
            log.debug(
                "Cloudinary upload started | type={} ownerId={} filename={} size={}",
                type,
                ownerId,
                file.getOriginalFilename(),
                file.getSize()
            );

            Map<?, ?> uploadResult = cloudinary
                .uploader()
                .upload(file.getBytes(), ObjectUtils.asMap("folder", folder, "resource_type", "auto"));

            String secureUrl = uploadResult.get("secure_url").toString();

            log.info("Cloudinary upload success | type={} ownerId={} url={}", type, ownerId, secureUrl);

            return secureUrl;
        } catch (Exception e) {
            log.error("Cloudinary upload failed | type={} ownerId={} filename={}", type, ownerId, file.getOriginalFilename(), e);
            throw new BadRequestAlertException("Cloudinary upload failed", "document", "cloudinaryUploadFailed");
        }
    }
}

package com.gearx7.app.service.interfacesImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gearx7.app.service.interfaces.DocumentStorageService;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryDocumentStorageServiceImpl implements DocumentStorageService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryDocumentStorageServiceImpl.class);

    private final Cloudinary cloudinary;

    // Allowed file extensions
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "webp", "pdf", "doc", "docx");

    // Image extensions
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("png", "jpg", "jpeg", "webp");

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

    @Override
    public String uploadCategoryImage(MultipartFile file, Long categoryId) {
        validate(file);

        String folder = "gearx-7/categories/category-" + categoryId + "/image";

        try {
            log.debug("Uploading category image | categoryId={}", categoryId);

            Map<?, ?> result = cloudinary
                .uploader()
                .upload(file.getBytes(), ObjectUtils.asMap("folder", folder, "resource_type", "image", "overwrite", true));

            String secureUrl = result.get("secure_url").toString();

            log.info("Image uploaded successfully | categoryId={} | url={}", categoryId, secureUrl);

            return secureUrl;
        } catch (IOException e) {
            log.error("Cloudinary upload failed | categoryId={}", categoryId, e);

            throw new BadRequestAlertException("Image upload failed", "category", "cloudinaryUploadFailed");
        }
    }

    @Override
    public String uploadSubcategoryImage(MultipartFile file, Long subcategoryId) {
        validate(file);
        String folder = "gearx-7/subcategories/subcategory-" + subcategoryId + "/image";

        try {
            log.debug("Uploading subcategory image");

            Map<?, ?> result = cloudinary
                .uploader()
                .upload(file.getBytes(), ObjectUtils.asMap("folder", folder, "resource_type", "image", "overwrite", true));

            String secureUrl = result.get("secure_url").toString();

            log.info("Subcategory image uploaded successfully | url={}", secureUrl);

            return secureUrl;
        } catch (IOException e) {
            log.error("Cloudinary upload failed for subcategory", e);

            throw new BadRequestAlertException("Subcategory image upload failed", "subcategory", "cloudinaryUploadFailed");
        }
    }

    /* ================= OPERATOR DOCUMENTS ================= */

    @Override
    public String uploadOperatorDocument(MultipartFile file, Long operatorId) {
        String folder = "gearx-7/operators/operator-" + operatorId + "/documents";

        return upload(file, folder, "OPERATOR", operatorId);
    }

    @Override
    public String uploadTypeImage(MultipartFile file, Long typeId) {
        validate(file);

        String folder = "gearx-7/types/type-" + typeId + "/image";

        try {
            log.debug("Uploading type image | typeId={}", typeId);

            Map<?, ?> result = cloudinary
                .uploader()
                .upload(file.getBytes(), ObjectUtils.asMap("folder", folder, "resource_type", "image", "overwrite", true));

            String secureUrl = result.get("secure_url").toString();

            log.info("Type image uploaded successfully | typeId={} | url={}", typeId, secureUrl);

            return secureUrl;
        } catch (IOException e) {
            log.error("Cloudinary upload failed | typeId={}", typeId, e);

            throw new BadRequestAlertException("Type image upload failed", "type", "cloudinaryUploadFailed");
        }
    }

    /* ================= COMMON UPLOAD LOGIC ================= */

    private String upload(MultipartFile file, String folder, String type, Long ownerId) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.contains(".")) {
            throw new BadRequestAlertException("Invalid file name", "document", "invalidFileName");
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestAlertException("Unsupported file type", "document", "invalidFileType");
        }

        String resourceType = IMAGE_EXTENSIONS.contains(extension) ? "image" : "raw";

        try {
            String publicId = folder + "/" + UUID.randomUUID();

            log.debug("Uploading file | type={} ownerId={} extension={} resourceType={}", type, ownerId, extension, resourceType);

            Map<?, ?> uploadResult = cloudinary
                .uploader()
                .upload(file.getBytes(), ObjectUtils.asMap("public_id", publicId, "resource_type", resourceType, "overwrite", true));

            String secureUrl = uploadResult.get("secure_url").toString();

            log.info("Upload successful | type={} ownerId={} url={}", type, ownerId, secureUrl);

            return secureUrl;
        } catch (IOException e) {
            log.error("Cloudinary upload failed | type={} ownerId={}", type, ownerId, e);
            throw new BadRequestAlertException("Cloudinary upload failed", "document", "cloudinaryUploadFailed");
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestAlertException("Image is required", "category", "Image Required");
        }

        String name = file.getOriginalFilename();

        if (name == null || !name.contains(".")) {
            throw new BadRequestAlertException("Invalid file name", "category", "InvalidFileType");
        }

        String ext = name.substring(name.lastIndexOf(".") + 1).toLowerCase();

        if (!IMAGE_EXTENSIONS.contains(ext)) {
            throw new BadRequestAlertException("Only image files allowed", "category", "InvalidFileType");
        }
    }
}

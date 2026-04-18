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
        validateImage(file);

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
        validateImage(file);
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
    public String uploadOperatorLicense(MultipartFile file, Long operatorId) {
        validateImage(file);

        String folder = "gearx-7/operators/operator-" + operatorId + "/license";

        try {
            Map<?, ?> result = cloudinary
                .uploader()
                .upload(file.getBytes(), ObjectUtils.asMap("folder", folder, "resource_type", "image", "overwrite", true));

            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new BadRequestAlertException("License upload failed", "operator", "cloudinaryUploadFailed");
        }
    }

    @Override
    public String uploadTypeImage(MultipartFile file, Long typeId) {
        validateImage(file);

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

    @Override
    public String uploadOperatorPhoto(MultipartFile file, Long operatorId) {
        validateImage(file);

        String folder = "gearx-7/operators/operator-" + operatorId + "/photo";

        try {
            log.debug("Uploading operator photo | operatorId={}", operatorId);

            Map<?, ?> result = cloudinary
                .uploader()
                .upload(file.getBytes(), ObjectUtils.asMap("folder", folder, "resource_type", "image", "overwrite", true));

            String secureUrl = result.get("secure_url").toString();

            log.info("Operator photo uploaded successfully | operatorId={} | url={}", operatorId, secureUrl);

            return secureUrl;
        } catch (IOException e) {
            log.error("Cloudinary photo upload failed | operatorId={}", operatorId, e);

            throw new BadRequestAlertException("Operator photo upload failed", "operator", "cloudinaryUploadFailed");
        }
    }

    @Override
    public void deleteByUrl(String url) {
        if (url == null || url.isBlank()) {
            log.debug("DELETE_BY_URL SKIPPED | empty url");
            return;
        }
        try {
            final String publicId = extractPublicId(url);

            // delete file
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("invalidate", true));

            log.info("DELETE_FILE SUCCESS | publicId={}", publicId);

            // child folder
            String childFolder = publicId.substring(0, publicId.lastIndexOf("/"));

            try {
                cloudinary.api().deleteFolder(childFolder, ObjectUtils.emptyMap());
                log.info("Deleted child folder | {}", childFolder);
            } catch (Exception ex) {
                log.warn("Child folder not empty | {}", childFolder);
            }

            // parent folder
            String parentFolder = childFolder.substring(0, childFolder.lastIndexOf("/"));

            try {
                cloudinary.api().deleteFolder(parentFolder, ObjectUtils.emptyMap());
                log.info("Deleted parent folder | {}", parentFolder);
            } catch (Exception ex) {
                log.warn("Parent folder not empty | {}", parentFolder);
            }
        } catch (Exception e) {
            log.warn("Cleanup failed | {}", url, e);
        }
    }

    private void validateImage(MultipartFile file) {
        String name = file.getOriginalFilename();

        if (name == null || !name.contains(".")) {
            log.error("Validation failed | Invalid filename");
            throw new BadRequestAlertException("Invalid file name", "file", "InvalidFileName");
        }

        String ext = name.substring(name.lastIndexOf(".") + 1).toLowerCase();

        if (!IMAGE_EXTENSIONS.contains(ext)) {
            log.error("Validation failed | Unsupported image type={}", ext);
            throw new BadRequestAlertException("Only image files allowed", "file", "InvalidFileType");
        }
    }

    @Override
    public void deleteOperatorFolder(Long operatorId) {
        if (operatorId == null) {
            log.warn("DELETE_OPERATOR_FOLDER skipped | operatorId is null");
            return;
        }

        final String baseFolder = "gearx-7/operators/operator-" + operatorId;

        final String photoFolder = baseFolder + "/photo";

        final String licenseFolder = baseFolder + "/license";

        log.info("DELETE_OPERATOR_FOLDER START | operatorId={}", operatorId);

        deleteFolderIfPossible(photoFolder, "PHOTO_FOLDER", operatorId);

        deleteFolderIfPossible(licenseFolder, "LICENSE_FOLDER", operatorId);

        deleteFolderIfPossible(baseFolder, "OPERATOR_FOLDER", operatorId);

        log.info("DELETE_OPERATOR_FOLDER COMPLETED | operatorId={}", operatorId);
    }

    private void deleteFolderIfPossible(String folderPath, String folderType, Long operatorId) {
        try {
            cloudinary.api().deleteFolder(folderPath, ObjectUtils.emptyMap());

            log.info("DELETE_FOLDER SUCCESS | operatorId={} | type={} | path={}", operatorId, folderType, folderPath);
        } catch (Exception ex) {
            log.debug(
                "DELETE_FOLDER SKIPPED | operatorId={} | type={} | path={} | reason={}",
                operatorId,
                folderType,
                folderPath,
                ex.getMessage()
            );
        }
    }

    /* ==========================================================
              EXTRACT CLOUDINARY PUBLIC ID FROM URL
   ========================================================== */

    private String extractPublicId(String url) {
        try {
            log.debug("Extracting Cloudinary publicId | url={}", url);

            /* --------------------------------------------------
           Find /upload/ segment
           Example:
           https://res.cloudinary.com/demo/image/upload/v12345/folder/file.png
           -------------------------------------------------- */

            int uploadIndex = url.indexOf("/upload/");

            if (uploadIndex < 0) {
                throw new IllegalArgumentException("Cloudinary upload segment not found");
            }

            /* --------------------------------------------------
           Take everything after /upload/
           Result:
           v12345/folder/file.png
           -------------------------------------------------- */

            String path = url.substring(uploadIndex + "/upload/".length());

            /* --------------------------------------------------
           Remove version if exists
           v12345/
           -------------------------------------------------- */

            path = path.replaceFirst("^v\\d+/", "");

            /* --------------------------------------------------
           Remove query params if any
           file.png?abc=123
           -------------------------------------------------- */

            int queryIndex = path.indexOf("?");

            if (queryIndex > -1) {
                path = path.substring(0, queryIndex);
            }

            /* --------------------------------------------------
           Remove file extension
           folder/file.png -> folder/file
           -------------------------------------------------- */

            int lastDot = path.lastIndexOf(".");

            if (lastDot > 0) {
                path = path.substring(0, lastDot);
            }

            log.info("Cloudinary publicId extracted successfully | publicId={}", path);

            return path;
        } catch (Exception ex) {
            log.error("Failed to extract Cloudinary publicId | url={}", url, ex);
            throw new BadRequestAlertException("Invalid Cloudinary URL", "cloudinary", "InvalidCloudinaryUrl");
        }
    }
}

package com.gearx7.app.service.interfacesImpl;

import com.gearx7.app.domain.MachineOperator;
import com.gearx7.app.domain.User;
import com.gearx7.app.repository.MachineOperatorRepository;
import com.gearx7.app.repository.UserRepository;
import com.gearx7.app.security.SecurityUtils;
import com.gearx7.app.service.dto.MachineOperatorDetailsDTO;
import com.gearx7.app.service.interfaces.DocumentStorageService;
import com.gearx7.app.service.interfaces.MachineOperatorService;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import com.gearx7.app.web.rest.errors.NotFoundAlertException;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class MachineOperatorServiceImpl implements MachineOperatorService {

    private static final Logger log = LoggerFactory.getLogger(MachineOperatorServiceImpl.class);

    private final MachineOperatorRepository operatorRepo;
    private final DocumentStorageService storageService;
    private final UserRepository userRepository;

    public MachineOperatorServiceImpl(
        MachineOperatorRepository operatorRepo,
        DocumentStorageService storageService,
        UserRepository userRepository
    ) {
        this.operatorRepo = operatorRepo;
        this.storageService = storageService;
        this.userRepository = userRepository;
    }

    /* ============================================================
                            CREATE
       ============================================================ */

    @Override
    public MachineOperatorDetailsDTO create(MachineOperatorDetailsDTO dto, MultipartFile operatorImage, MultipartFile license) {
        log.info(
            "CREATE Operator START | photo={} | license={}",
            operatorImage != null && !operatorImage.isEmpty(),
            license != null && !license.isEmpty()
        );

        User partner = getCurrentPartner();

        validateCreateRequest(operatorImage, license);

        // ==============================
        //      CREATE OPERATOR
        // ==============================
        MachineOperator operator = new MachineOperator();

        operator.setDriverName(dto.getDriverName());
        operator.setOperatorContact(dto.getOperatorContact());
        operator.setAddress(dto.getAddress());
        operator.setLicenseIssueDate(dto.getLicenseIssueDate());
        operator.setCreatedAt(Instant.now());
        operator.setPartner(partner);

        operator = operatorRepo.saveAndFlush(operator);

        log.info("Operator temp saved | operatorId={}", operator.getId());

        String photoUrl = null;
        String licenseUrl = null;

        try {
            // Upload photo
            photoUrl = storageService.uploadOperatorPhoto(operatorImage, operator.getId());
            operator.setImageUrl(photoUrl);

            // Upload license
            licenseUrl = storageService.uploadOperatorLicense(license, operator.getId());
            operator.setDocUrl(licenseUrl);

            operator = operatorRepo.saveAndFlush(operator);

            log.info("Create Operator SUCCESS | operatorId={}", operator.getId());

            return mapToDTO(operator);
        } catch (Exception ex) {
            log.error("Create Operator FAILED | operatorId={} | reason={}", operator.getId(), ex.getMessage(), ex);

            // cleanup
            safeDeleteCloudinary(photoUrl, "photo", operator.getId());
            safeDeleteCloudinary(licenseUrl, "license", operator.getId());

            try {
                storageService.deleteOperatorFolder(operator.getId());
            } catch (Exception cleanupEx) {
                log.warn("Folder cleanup failed | operatorId={}", operator.getId());
            }

            throw new BadRequestAlertException("Operator creation failed", "MachineOperator", "CreateFailed");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MachineOperatorDetailsDTO> getAllOperatorsByPartner() {
        User partner = getCurrentPartner();

        log.info("GET Operators By Partner START | partnerId={} | login={}", partner.getId(), partner.getLogin());

        List<MachineOperator> operators = operatorRepo.findByPartnerIdWithRelations(partner.getId());

        log.info("GET Operators By Partner SUCCESS | partnerId={} | count={}", partner.getId(), operators.size());

        return operators.stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MachineOperatorDetailsDTO partialUpdate(
        Long operatorId,
        MachineOperatorDetailsDTO dto,
        MultipartFile photo,
        MultipartFile license
    ) {
        log.info("PATCH Operator START | operatorId={}", operatorId);

        MachineOperator operator = operatorRepo
            .findById(operatorId)
            .orElseThrow(() -> {
                log.error("Operator NOT FOUND | operatorId={}", operatorId);
                return new NotFoundAlertException("Operator not found", "MachineOperator", "OperatorNotFound");
            });

        String oldPhoto = operator.getImageUrl();
        String oldLicense = operator.getDocUrl();

        String newPhoto = null;
        String newLicense = null;

        try {
            // =========================
            // BASIC FIELD UPDATES
            // =========================
            if (dto.getDriverName() != null) operator.setDriverName(dto.getDriverName());
            if (dto.getOperatorContact() != null) operator.setOperatorContact(dto.getOperatorContact());
            if (dto.getAddress() != null) operator.setAddress(dto.getAddress());
            if (dto.getLicenseIssueDate() != null) operator.setLicenseIssueDate(dto.getLicenseIssueDate());

            // =========================
            // FILE UPDATES
            // =========================
            if (photo != null && !photo.isEmpty()) {
                newPhoto = storageService.uploadOperatorPhoto(photo, operator.getId());
                operator.setImageUrl(newPhoto);
            }

            if (license != null && !license.isEmpty()) {
                newLicense = storageService.uploadOperatorLicense(license, operator.getId());
                operator.setDocUrl(newLicense);
            }

            operator = operatorRepo.saveAndFlush(operator);

            // =========================
            // CLEANUP OLD FILES
            // =========================
            if (newPhoto != null && oldPhoto != null) {
                safeDeleteCloudinary(oldPhoto, "old-photo", operatorId);
            }

            if (newLicense != null && oldLicense != null) {
                safeDeleteCloudinary(oldLicense, "old-license", operatorId);
            }

            log.info("PATCH Operator SUCCESS | operatorId={}", operatorId);

            return mapToDTO(operator);
        } catch (Exception ex) {
            log.error("PATCH Operator FAILED | operatorId={} | reason={}", operatorId, ex.getMessage(), ex);

            safeDeleteCloudinary(newPhoto, "new-photo", operatorId);
            safeDeleteCloudinary(newLicense, "new-license", operatorId);

            throw new BadRequestAlertException("Operator patch update failed", "MachineOperator", "PatchFailed");
        }
    }

    /* ============================================================
                            GET ALL Operators
       ============================================================ */
    @Override
    @Transactional(readOnly = true)
    public List<MachineOperatorDetailsDTO> getAllOperators() {
        log.info("GET ALL Operators START");

        List<MachineOperator> operators = operatorRepo.findAllWithRelations();

        log.info("GET ALL Operators SUCCESS | count={}", operators.size());

        return operators.stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MachineOperatorDetailsDTO update(Long operatorId, MachineOperatorDetailsDTO dto, MultipartFile photo, MultipartFile license) {
        log.info("PUT Operator START | operatorId={}", operatorId);

        MachineOperator operator = operatorRepo
            .findById(operatorId)
            .orElseThrow(() -> {
                log.error("Operator NOT FOUND | operatorId={}", operatorId);
                return new NotFoundAlertException("Operator not found", "MachineOperator", "OperatorNotFound");
            });

        String oldPhoto = operator.getImageUrl();
        String oldLicense = operator.getDocUrl();

        String newPhoto = null;
        String newLicense = null;

        try {
            // =========================
            // FULL REPLACEMENT (PUT)
            // =========================
            operator.setDriverName(dto.getDriverName());
            operator.setOperatorContact(dto.getOperatorContact());
            operator.setAddress(dto.getAddress());
            operator.setLicenseIssueDate(dto.getLicenseIssueDate());

            // =========================
            // FILE UPDATES
            // =========================
            if (photo != null && !photo.isEmpty()) {
                newPhoto = storageService.uploadOperatorPhoto(photo, operator.getId());
                operator.setImageUrl(newPhoto);
            }

            if (license != null && !license.isEmpty()) {
                newLicense = storageService.uploadOperatorLicense(license, operator.getId());
                operator.setDocUrl(newLicense);
            }

            operator = operatorRepo.saveAndFlush(operator);

            // cleanup old files
            if (newPhoto != null && oldPhoto != null) {
                safeDeleteCloudinary(oldPhoto, "old-photo", operatorId);
            }

            if (newLicense != null && oldLicense != null) {
                safeDeleteCloudinary(oldLicense, "old-license", operatorId);
            }

            log.info("PUT Operator SUCCESS | operatorId={}", operatorId);

            return mapToDTO(operator);
        } catch (Exception ex) {
            log.error("PUT Operator FAILED | operatorId={} | reason={}", operatorId, ex.getMessage(), ex);

            safeDeleteCloudinary(newPhoto, "new-photo", operatorId);
            safeDeleteCloudinary(newLicense, "new-license", operatorId);

            throw new BadRequestAlertException("Operator update failed", "MachineOperator", "UpdateFailed");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MachineOperatorDetailsDTO getById(Long operatorId) {
        log.info("GET Operator BY ID START | operatorId={}", operatorId);

        MachineOperator operator = operatorRepo
            .findOneWithRelations(operatorId)
            .orElseThrow(() -> {
                log.error("Operator NOT FOUND | operatorId={}", operatorId);
                return new NotFoundAlertException("Operator not found", "MachineOperator", "OperatorNotFound");
            });

        log.info("GET Operator BY ID SUCCESS | operatorId={}", operatorId);

        return mapToDTO(operator);
    }

    /* ============================================================
                          INTERNAL HELPERS
       ============================================================ */

    private MachineOperatorDetailsDTO mapToDTO(MachineOperator operator) {
        MachineOperatorDetailsDTO dto = new MachineOperatorDetailsDTO();

        dto.setOperatorId(operator.getId());
        dto.setDriverName(operator.getDriverName());
        dto.setOperatorContact(operator.getOperatorContact());
        dto.setAddress(operator.getAddress());
        dto.setLicenseIssueDate(operator.getLicenseIssueDate());
        dto.setCreatedAt(operator.getCreatedAt());
        dto.setDocUrl(operator.getDocUrl());
        dto.setImageUrl(operator.getImageUrl());

        if (operator.getPartner() != null) {
            dto.setPartnerId(operator.getPartner().getId());
        }

        if (operator.getMachine() != null) {
            dto.setMachineId(operator.getMachine().getId());
            dto.setActive(true);
        } else {
            dto.setMachineId(null);
            dto.setActive(false);
        }

        return dto;
    }

    @Override
    public void delete(Long operatorId) {
        log.info("DELETE Operator START | operatorId={}", operatorId);

        MachineOperator operator = operatorRepo
            .findById(operatorId)
            .orElseThrow(() -> {
                log.error("DELETE FAILED | Operator NOT FOUND | operatorId={}", operatorId);
                return new NotFoundAlertException("Operator not found", "MachineOperator", "OperatorNotFound");
            });

        try {
            safeDeleteCloudinary(operator.getImageUrl(), "photo", operatorId);
            safeDeleteCloudinary(operator.getDocUrl(), "license", operatorId);

            storageService.deleteOperatorFolder(operatorId);

            operatorRepo.delete(operator);

            log.info("DELETE Operator SUCCESS | operatorId={}", operatorId);
        } catch (Exception ex) {
            log.error("DELETE Operator FAILED | operatorId={} | reason={}", operatorId, ex.getMessage(), ex);
            throw new BadRequestAlertException("Operator delete failed", "MachineOperator", "DeleteFailed");
        }
    }

    /* ==========================================================
                       REQUEST VALIDATION
   ========================================================== */

    private void validateCreateRequest(MultipartFile operatorImage, MultipartFile license) {
        if (operatorImage == null || operatorImage.isEmpty()) {
            log.error("Validation failed | Operator image missing");

            throw new BadRequestAlertException("Operator image is required", "MachineOperator", "OperatorImageMissing");
        }

        if (license == null || license.isEmpty()) {
            log.error("Validation failed | Operator license missing");
            throw new BadRequestAlertException("Operator license is required", "MachineOperator", "OperatorLicenseMissing");
        }
    }

    /* ==========================================================
                  SAFE DELETE CLOUDINARY FILE

   ========================================================== */

    private void safeDeleteCloudinary(String url, String fileType, Long operatorId) {
        if (url == null || url.isBlank()) {
            return;
        }
        try {
            storageService.deleteByUrl(url);
            log.info("Cleanup success | operatorId={} | type={} | url={}", operatorId, fileType, url);
        } catch (Exception ex) {
            log.error("Cleanup failed | operatorId={} | type={} | url={}", operatorId, fileType, url, ex);
        }
    }

    private User getCurrentPartner() {
        String login = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("User not logged in", "User", "NotAuthenticated"));

        return userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("User not found", "User", "UserNotFound"));
    }
}

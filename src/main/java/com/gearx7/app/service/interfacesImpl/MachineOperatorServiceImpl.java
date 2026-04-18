package com.gearx7.app.service.interfacesImpl;

import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.MachineOperator;
import com.gearx7.app.repository.MachineOperatorRepository;
import com.gearx7.app.repository.MachineRepository;
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
    private final MachineRepository machineRepo;
    private final DocumentStorageService storageService;

    public MachineOperatorServiceImpl(
        MachineOperatorRepository operatorRepo,
        MachineRepository machineRepo,
        DocumentStorageService storageService
    ) {
        this.operatorRepo = operatorRepo;
        this.machineRepo = machineRepo;
        this.storageService = storageService;
    }

    /* ============================================================
                            CREATE
       ============================================================ */

    @Override
    public MachineOperatorDetailsDTO create(MachineOperatorDetailsDTO dto, MultipartFile operatorImage, MultipartFile license) {
        log.info("Request to create operator | machineId={}", dto.getMachineId());

        validateCreateRequest(operatorImage, license);

        Machine machine = getMachineOrThrow(dto.getMachineId());

        validateNewAssignment(machine.getId());

        MachineOperator operator = buildOperator(machine, dto);

        operator = operatorRepo.saveAndFlush(operator);

        log.debug("Temporary operator row created | operatorId={} | machineId={}", operator.getId(), machine.getId());

        String photoUrl = null;
        String licenseUrl = null;

        try {
            // Upload photo
            photoUrl = storageService.uploadOperatorPhoto(operatorImage, operator.getId());

            log.info("Operator photo uploaded | operatorId={} | url={}", operator.getId(), photoUrl);
            operator.setImageUrl(photoUrl);

            // Upload license
            licenseUrl = storageService.uploadOperatorLicense(license, operator.getId());

            log.info("Operator license uploaded | operatorId={} | url={}", operator.getId(), licenseUrl);

            operator.setDocUrl(licenseUrl);
            operator.setActive(true);

            operator = operatorRepo.saveAndFlush(operator);

            log.info("Create Operator SUCCESS | operatorId={} | machineId={}", operator.getId(), machine.getId());

            return mapToDTO(operator);
        } catch (Exception ex) {
            log.error("Create Operator FAILED | operatorId={} | reason={}", operator.getId(), ex.getMessage(), ex);

            //========  CLEANUP CLOUDINARY FILES =========

            safeDeleteCloudinary(photoUrl, "photo", operator.getId());
            safeDeleteCloudinary(licenseUrl, "license", operator.getId());

            try {
                storageService.deleteOperatorFolder(operator.getId());
            } catch (Exception e) {
                log.warn("Folder cleanup failed | operatorId={}", operator.getId());
            }

            throw new BadRequestAlertException("Operator creation failed", "MachineOperator", "CreateFailed");
        }
    }

    /* ============================================================
                            REASSIGN
       ============================================================ */

    @Override
    public MachineOperatorDetailsDTO reassign(
        Long machineId,
        MachineOperatorDetailsDTO dto,
        MultipartFile operatorImage,
        MultipartFile license
    ) {
        log.info("Request to reassign operator | machineId={}", machineId);

        Machine machine = getMachineOrThrow(machineId);

        deactivateExistingAssignments(machineId);

        MachineOperator operator = buildOperator(machine, dto);

        operator = operatorRepo.save(operator);

        String photoUrl = null;
        String licenseUrl = null;

        try {
            if (operatorImage != null && !operatorImage.isEmpty()) {
                photoUrl = storageService.uploadOperatorPhoto(operatorImage, operator.getId());
                operator.setImageUrl(photoUrl);
            }

            if (license != null && !license.isEmpty()) {
                licenseUrl = storageService.uploadOperatorLicense(license, operator.getId());
                operator.setDocUrl(licenseUrl);
            }
            operator = operatorRepo.saveAndFlush(operator);

            log.info("Reassign SUCCESS | operatorId={} | machineId={}", operator.getId(), machineId);

            return mapToDTO(operator);
        } catch (Exception ex) {
            log.error("Reassign FAILED | operatorId={}", operator.getId(), ex);

            safeDeleteCloudinary(photoUrl, "photo", operator.getId());
            safeDeleteCloudinary(licenseUrl, "license", operator.getId());
            storageService.deleteOperatorFolder(operator.getId());

            throw new BadRequestAlertException("Reassign Operator creation failed", "MachineOperator", "CreateFailed");
        }
    }

    /* ============================================================
                                GET
       ============================================================ */

    @Override
    @Transactional(readOnly = true)
    public MachineOperatorDetailsDTO getByMachineId(Long machineId) {
        log.debug("Fetching active operator | machineId={}", machineId);

        MachineOperator operator = operatorRepo
            .findByMachineIdAndActiveTrue(machineId)
            .orElseThrow(() ->
                new NotFoundAlertException("No active operator for machine id : " + machineId, "MachineOperator", "OperatorNotFound")
            );

        return mapToDTO(operator);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MachineOperatorDetailsDTO> getAllActiveOperators() {
        log.debug("Fetching all active operators");

        List<MachineOperator> operators = operatorRepo.findAllByActiveTrue();

        log.debug("Active operators found | count={}", operators.size());

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
        log.info("PATCH Operator request START | operatorId={}", operatorId);

        MachineOperator operator = operatorRepo
            .findById(operatorId)
            .orElseThrow(() -> {
                log.error("Operator not found | operatorId={}", operatorId);
                return new NotFoundAlertException("Operator not found", "MachineOperator", "OperatorNotFound");
            });

        String oldPhotoUrl = operator.getImageUrl();
        String oldLicenseUrl = operator.getDocUrl();

        String newPhotoUrl = null;
        String newLicenseUrl = null;

        try {
            if (dto.getDriverName() != null) {
                operator.setDriverName(dto.getDriverName());
            }

            if (dto.getOperatorContact() != null) {
                operator.setOperatorContact(dto.getOperatorContact());
            }

            if (dto.getAddress() != null) {
                operator.setAddress(dto.getAddress());
            }

            if (dto.getLicenseIssueDate() != null) {
                operator.setLicenseIssueDate(dto.getLicenseIssueDate());
            }

            if (photo != null && !photo.isEmpty()) {
                newPhotoUrl = storageService.uploadOperatorPhoto(photo, operator.getId());
                operator.setImageUrl(newPhotoUrl);
                log.info("Operator photo updated | operatorId={}", operatorId);
            }

            if (license != null && !license.isEmpty()) {
                newLicenseUrl = storageService.uploadOperatorLicense(license, operator.getId());
                operator.setDocUrl(newLicenseUrl);
                log.info("Operator license updated | operatorId={}", operatorId);
            }
            operator = operatorRepo.saveAndFlush(operator);

            /* ==============================================
           DELETE OLD FILES AFTER SUCCESS
           ============================================== */

            if (newPhotoUrl != null && oldPhotoUrl != null) {
                safeDeleteCloudinary(oldPhotoUrl, "old-photo", operatorId);
            }

            if (newLicenseUrl != null && oldLicenseUrl != null) {
                safeDeleteCloudinary(oldLicenseUrl, "old-license", operatorId);
            }

            log.info("PATCH Operator SUCCESS | operatorId={}", operatorId);

            return mapToDTO(operator);
        } catch (Exception ex) {
            log.error("PATCH Operator FAILED | operatorId={} | reason={}", operatorId, ex.getMessage(), ex);

            /* cleanup newly uploaded files */
            safeDeleteCloudinary(newPhotoUrl, "new-photo", operatorId);

            safeDeleteCloudinary(newLicenseUrl, "new-license", operatorId);

            throw new BadRequestAlertException("Operator patch update failed", "MachineOperator", "PatchFailed");
        }
    }

    /* ============================================================
                          INTERNAL HELPERS
       ============================================================ */

    private Machine getMachineOrThrow(Long machineId) {
        return machineRepo
            .findById(machineId)
            .orElseThrow(() -> {
                log.error("Machine not found | machineId={}", machineId);
                return new NotFoundAlertException("Machine not found", "Machine", "MachineNotFound");
            });
    }

    private void validateNewAssignment(Long machineId) {
        if (operatorRepo.existsByMachineIdAndActiveTrue(machineId)) {
            log.warn("Machine already has active operator | machineId={}", machineId);
            throw new BadRequestAlertException("Machine already has operator", "MachineOperator", "MachineAlreadyHasOperator");
        }
    }

    private void deactivateExistingAssignments(Long machineId) {
        operatorRepo.deactivateAllActiveByMachineId(machineId);
    }

    private MachineOperator buildOperator(Machine machine, MachineOperatorDetailsDTO dto) {
        MachineOperator operator = new MachineOperator();
        operator.setMachine(machine);
        operator.setDriverName(dto.getDriverName());
        operator.setOperatorContact(dto.getOperatorContact());
        operator.setAddress(dto.getAddress());
        operator.setLicenseIssueDate(dto.getLicenseIssueDate());
        operator.setActive(true);
        operator.setCreatedAt(Instant.now());

        return operator;
    }

    private MachineOperatorDetailsDTO mapToDTO(MachineOperator operator) {
        MachineOperatorDetailsDTO dto = new MachineOperatorDetailsDTO();

        dto.setOperatorId(operator.getId());
        dto.setMachineId(operator.getMachine().getId());
        dto.setDriverName(operator.getDriverName());
        dto.setOperatorContact(operator.getOperatorContact());
        dto.setAddress(operator.getAddress());
        dto.setActive(operator.getActive());
        dto.setLicenseIssueDate(operator.getLicenseIssueDate());
        dto.setCreatedAt(operator.getCreatedAt());
        dto.setDocUrl(operator.getDocUrl());
        dto.setImageUrl(operator.getImageUrl());

        return dto;
    }

    @Override
    public void delete(Long operatorId) {
        MachineOperator operator = operatorRepo
            .findById(operatorId)
            .orElseThrow(() -> new NotFoundAlertException("Operator not found", "MachineOperator", "OperatorNotFound"));

        safeDeleteCloudinary(operator.getImageUrl(), "photo", operatorId);

        safeDeleteCloudinary(operator.getDocUrl(), "license", operatorId);

        storageService.deleteOperatorFolder(operatorId);

        operatorRepo.delete(operator);

        log.info("DELETE Operator SUCCESS | operatorId={}", operatorId);
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
}

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
        log.info(
            "CREATE Operator START | machineId={} | photo={} | license={}",
            dto.getMachineId(),
            operatorImage != null && !operatorImage.isEmpty(),
            license != null && !license.isEmpty()
        );

        validateCreateRequest(operatorImage, license);

        Machine machine = null;

        if (dto.getMachineId() != null) {
            machine = getMachineOrThrow(dto.getMachineId());
            validateNewAssignment(dto.getMachineId());
        }

        MachineOperator operator = buildOperator(machine, dto);

        operator = operatorRepo.saveAndFlush(operator);

        log.debug("Operator temp saved | operatorId={} | machineId={}", operator.getId(), machine != null ? machine.getId() : null);

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
            operator.setActive(machine != null);

            operator = operatorRepo.saveAndFlush(operator);

            log.info("Create Operator SUCCESS | operatorId={} | machineId={}", operator.getId(), machine != null ? machine.getId() : null);

            return mapToDTO(operator);
        } catch (Exception ex) {
            log.error("Create Operator FAILED | operatorId={} | reason={}", operator.getId(), ex.getMessage(), ex);

            //========  CLEANUP CLOUDINARY FILES =========

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

    /* ============================================================
                            REASSIGN
       ============================================================ */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MachineOperatorDetailsDTO reassign(Long machineId, MachineOperatorDetailsDTO dto, MultipartFile photo, MultipartFile license) {
        log.info("reassign start | operatorId={} | machineId={}", dto.getOperatorId(), machineId);

        // Find existing or create new
        MachineOperator operator;
        boolean isUpdate = dto.getOperatorId() != null;

        if (isUpdate) {
            operator =
                operatorRepo
                    .findById(dto.getOperatorId())
                    .orElseThrow(() -> {
                        log.error("Operator not found | operatorId={}", dto.getOperatorId());
                        return new NotFoundAlertException("Operator not found", "MachineOperator", "OperatorNotFound");
                    });
        } else {
            operator = new MachineOperator();
            operator.setCreatedAt(Instant.now());
        }

        //  Basic fields
        if (dto.getDriverName() != null) operator.setDriverName(dto.getDriverName());
        if (dto.getOperatorContact() != null) operator.setOperatorContact(dto.getOperatorContact());
        if (dto.getAddress() != null) operator.setAddress(dto.getAddress());
        if (dto.getLicenseIssueDate() != null) operator.setLicenseIssueDate(dto.getLicenseIssueDate());

        String oldPhoto = operator.getImageUrl();
        String oldLicense = operator.getDocUrl();

        String newPhoto = null;
        String newLicense = null;

        try {
            //  CASE 1: machineId present → assign
            if (machineId != null) {
                Machine machine = getMachineOrThrow(machineId);

                // only deactivate if another active operator exists
                if (operatorRepo.existsByMachineIdAndActiveTrue(machineId)) {
                    deactivateExistingAssignments(machineId);
                }

                operator.setMachine(machine);
                operator.setActive(true);

                log.debug(
                    "Operator assigned | operatorId={} | machineId={}",
                    operator.getId() != null ? operator.getId() : "NEW",
                    machineId
                );
            }
            //  CASE 2: machineId NOT present → unassign / standalone
            else {
                operator.setMachine(null);
                operator.setActive(false);

                log.debug("Operator set as standalone | operatorId={}", operator.getId());
            }

            operator = operatorRepo.saveAndFlush(operator);

            //  File uploads
            if (photo != null && !photo.isEmpty()) {
                newPhoto = storageService.uploadOperatorPhoto(photo, operator.getId());
                operator.setImageUrl(newPhoto);
            }

            if (license != null && !license.isEmpty()) {
                newLicense = storageService.uploadOperatorLicense(license, operator.getId());
                operator.setDocUrl(newLicense);
            }

            operator = operatorRepo.saveAndFlush(operator);

            //  Cleanup old files after success
            if (newPhoto != null && oldPhoto != null) {
                safeDeleteCloudinary(oldPhoto, "old-photo", operator.getId());
            }
            if (newLicense != null && oldLicense != null) {
                safeDeleteCloudinary(oldLicense, "old-license", operator.getId());
            }

            log.info("REASSIGN  SUCCESS | operatorId={} | machineId={}", operator.getId(), machineId);

            return mapToDTO(operator);
        } catch (Exception ex) {
            log.error("Reassign FAILED | operatorId={} | machineId={} | reason={}", operator.getId(), machineId, ex.getMessage(), ex);

            // cleanup new uploads
            safeDeleteCloudinary(newPhoto, "new-photo", operator.getId());
            safeDeleteCloudinary(newLicense, "new-license", operator.getId());

            throw new BadRequestAlertException("Operator upsert failed", "MachineOperator", "UpsertFailed");
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
        log.info("PATCH START | operatorId={} | machineId={}", operatorId, dto.getMachineId());

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
            /* =========================
           BASIC FIELD UPDATES
        ========================= */

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

            /* =========================
           MACHINE HANDLING
        ========================= */

            // CASE 1: Assign / Reassign
            if (dto.getMachineId() != null) {
                Machine machine = getMachineOrThrow(dto.getMachineId());

                if (!machine.getId().equals(operator.getMachine() != null ? operator.getMachine().getId() : null)) {
                    deactivateExistingAssignments(machine.getId());
                }
                operator.setMachine(machine);
                operator.setActive(true);

                log.info("Operator assigned | operatorId={} | machineId={}", operatorId, dto.getMachineId());
            }

            //  CASE 2: Unassign (explicit flag recommended)
            // Example: dto.setActive(false) or add dto.isUnassign()
            if (dto.getMachineId() == null && Boolean.FALSE.equals(dto.getActive())) {
                operator.setMachine(null);
                operator.setActive(false);

                log.info("Operator unassigned | operatorId={}", operatorId);
            }

            /* =========================
           FILE UPLOADS
        ========================= */

            if (photo != null && !photo.isEmpty()) {
                newPhotoUrl = storageService.uploadOperatorPhoto(photo, operator.getId());
                operator.setImageUrl(newPhotoUrl);

                log.debug("Photo updated | operatorId={}", operatorId);
            }

            if (license != null && !license.isEmpty()) {
                newLicenseUrl = storageService.uploadOperatorLicense(license, operator.getId());
                operator.setDocUrl(newLicenseUrl);

                log.debug("License updated | operatorId={}", operatorId);
            }

            operator = operatorRepo.saveAndFlush(operator);

            /* =========================
           CLEANUP OLD FILES
        ========================= */

            if (newPhotoUrl != null && oldPhotoUrl != null) {
                safeDeleteCloudinary(oldPhotoUrl, "old-photo", operatorId);
            }

            if (newLicenseUrl != null && oldLicenseUrl != null) {
                safeDeleteCloudinary(oldLicenseUrl, "old-license", operatorId);
            }

            log.info(
                "PATCH SUCCESS | operatorId={} | machineId={}",
                operatorId,
                operator.getMachine() != null ? operator.getMachine().getId() : null
            );

            return mapToDTO(operator);
        } catch (Exception ex) {
            log.error("PATCH FAILED | operatorId={} | reason={}", operatorId, ex.getMessage(), ex);

            safeDeleteCloudinary(newPhotoUrl, "new-photo", operatorId);
            safeDeleteCloudinary(newLicenseUrl, "new-license", operatorId);

            throw new BadRequestAlertException("Operator patch update failed", "MachineOperator", "PatchFailed");
        }
    }

    /* ============================================================
                            GET ALL Operators
       ============================================================ */
    @Override
    @Transactional(readOnly = true)
    public List<MachineOperatorDetailsDTO> getAllOperators() {
        log.info("Fetching ALL operators from DB");

        List<MachineOperator> operators = operatorRepo.findAll();

        log.info("Operators fetched | count={}", operators.size());

        return operators.stream().map(this::mapToDTO).toList();
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
        operator.setActive(false);
        operator.setCreatedAt(Instant.now());

        return operator;
    }

    private MachineOperatorDetailsDTO mapToDTO(MachineOperator operator) {
        MachineOperatorDetailsDTO dto = new MachineOperatorDetailsDTO();

        dto.setOperatorId(operator.getId());
        if (operator.getMachine() != null) {
            dto.setMachineId(operator.getMachine().getId());
        }
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

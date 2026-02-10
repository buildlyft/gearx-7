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
    public MachineOperatorDetailsDTO create(MachineOperatorDetailsDTO dto, MultipartFile file) {
        log.info("Request to create operator | machineId={}", dto.getMachineId());

        Machine machine = getMachineOrThrow(dto.getMachineId());

        validateNewAssignment(machine.getId());

        MachineOperator operator = buildOperator(machine, dto);

        operator = operatorRepo.save(operator);

        log.debug("Operator saved | operatorId={}", operator.getId());

        attachDocumentIfPresent(operator, file);

        log.info("Operator successfully created | operatorId={} machineId={}", operator.getId(), machine.getId());

        return mapToDTO(operator);
    }

    /* ============================================================
                            REASSIGN
       ============================================================ */

    @Override
    public MachineOperatorDetailsDTO reassign(Long machineId, MachineOperatorDetailsDTO dto, MultipartFile file) {
        log.info("Request to reassign operator | machineId={}", machineId);

        Machine machine = getMachineOrThrow(machineId);

        deactivateExistingAssignments(machineId);

        MachineOperator operator = buildOperator(machine, dto);

        operator = operatorRepo.save(operator);

        log.debug("New operator assigned | operatorId={}", operator.getId());

        attachDocumentIfPresent(operator, file);

        return mapToDTO(operator);
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
        operatorRepo
            .findByMachineIdAndActiveTrue(machineId)
            .ifPresent(existing -> {
                existing.setActive(false);
                log.info("Previous operator deactivated | operatorId={}", existing.getId());
            });
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

    private void attachDocumentIfPresent(MachineOperator operator, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.debug("No document provided for operatorId={}", operator.getId());
            return;
        }
        log.debug("Uploading document for operatorId={}", operator.getId());

        String url = storageService.uploadOperatorDocument(file, operator.getId());

        operator.setDocUrl(url);
        operatorRepo.save(operator);

        log.info("Document uploaded | operatorId={} docUrl={}", operator.getId(), url);
    }

    private MachineOperatorDetailsDTO mapToDTO(MachineOperator operator) {
        MachineOperatorDetailsDTO dto = new MachineOperatorDetailsDTO();

        dto.setOperatorId(operator.getId());
        dto.setMachineId(operator.getMachine().getId());
        dto.setDriverName(operator.getDriverName());
        dto.setOperatorContact(operator.getOperatorContact());
        dto.setAddress(operator.getAddress());
        dto.setLicenseIssueDate(operator.getLicenseIssueDate());
        dto.setCreatedAt(operator.getCreatedAt());
        dto.setDocUrl(operator.getDocUrl());

        return dto;
    }
}

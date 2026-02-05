package com.gearx7.app.service.interfacesImpl;

import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.MachineOperator;
import com.gearx7.app.domain.User;
import com.gearx7.app.domain.VehicleDocument;
import com.gearx7.app.repository.MachineOperatorRepository;
import com.gearx7.app.repository.MachineRepository;
import com.gearx7.app.repository.UserRepository;
import com.gearx7.app.repository.VehicleDocumentRepository;
import com.gearx7.app.service.dto.MachineOperatorDetailsDTO;
import com.gearx7.app.service.dto.OperatorDocumentDTO;
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
    private final UserRepository userRepo;
    private final VehicleDocumentRepository documentRepo;
    private final DocumentStorageService storageService;

    public MachineOperatorServiceImpl(
        MachineOperatorRepository operatorRepo,
        MachineRepository machineRepo,
        UserRepository userRepo,
        VehicleDocumentRepository documentRepo,
        DocumentStorageService storageService
    ) {
        this.operatorRepo = operatorRepo;
        this.machineRepo = machineRepo;
        this.userRepo = userRepo;
        this.documentRepo = documentRepo;
        this.storageService = storageService;
    }

    /* ================= CREATE ================= */

    @Override
    public MachineOperatorDetailsDTO create(MachineOperatorDetailsDTO dto, List<MultipartFile> files) {
        log.info("Create operator | machineId={} userId={}", dto.getMachineId(), dto.getUserId());

        Machine machine = getMachine(dto.getMachineId());
        User user = getUser(dto.getUserId());

        validateNewAssignment(machine.getId(), user.getId());

        MachineOperator operator = saveOperator(machine, user, dto);

        saveDocuments(operator, machine, user, files);

        return buildResponse(operator);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MachineOperatorDetailsDTO> getAllActiveOperators() {
        log.info("Fetching all active machine operators");

        List<MachineOperator> operators = operatorRepo.findAllByActiveTrue();

        if (operators.isEmpty()) {
            log.info("No active operators found");
            return List.of();
        }

        log.info("Active operators fetched | count={}", operators.size());

        return operators
            .stream()
            .map(operator -> {
                log.trace("Building response for operatorId={}", operator.getId());
                return buildResponse(operator);
            })
            .toList();
    }

    /* ================= REASSIGN ================= */

    @Override
    public MachineOperatorDetailsDTO reassign(Long machineId, MachineOperatorDetailsDTO dto, List<MultipartFile> files) {
        Machine machine = getMachine(machineId);
        User user = getUser(dto.getUserId());

        deactivateExistingAssignments(machineId, user.getId());

        MachineOperator operator = saveOperator(machine, user, dto);

        saveDocuments(operator, machine, user, files);

        return buildResponse(operator);
    }

    /* ================= GET ================= */

    @Override
    @Transactional(readOnly = true)
    public MachineOperatorDetailsDTO getByMachineId(Long machineId) {
        machineRepo
            .findById(machineId)
            .orElseThrow(() -> new NotFoundAlertException("Machine not found with id : " + machineId, "Machine", "MachineNotFound"));

        MachineOperator operator = operatorRepo
            .findByMachineIdAndActiveTrue(machineId)
            .orElseThrow(() ->
                new NotFoundAlertException("No active operator for machine id :" + machineId, "MachineOperator", "OperatorNotFound")
            );

        return buildResponse(operator);
    }

    /* ================= INTERNAL ================= */

    private MachineOperator saveOperator(Machine machine, User user, MachineOperatorDetailsDTO dto) {
        MachineOperator operator = new MachineOperator();
        operator.setMachine(machine);
        operator.setUser(user);
        operator.setDriverName(dto.getDriverName());
        operator.setOperatorContact(dto.getOperatorContact());
        operator.setAddress(dto.getAddress());
        operator.setLicenseIssueDate(dto.getLicenseIssueDate());
        operator.setActive(true);
        operator.setCreatedAt(Instant.now());

        return operatorRepo.save(operator);
    }

    private void saveDocuments(MachineOperator operator, Machine machine, User user, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;

        for (MultipartFile file : files) {
            String docType = extractDocType(file.getOriginalFilename());
            String url = storageService.uploadOperatorDocument(file, operator.getId());

            VehicleDocument doc = new VehicleDocument();
            doc.setOperator(operator);
            doc.setMachine(machine);
            doc.setDocType(docType);
            doc.setFileKey(url);
            doc.setContentType(file.getContentType());
            doc.setUploadedBy(user);
            doc.setUploadedAt(Instant.now());

            documentRepo.save(doc);
        }
    }

    private String extractDocType(String filename) {
        if (filename == null) return "UNKNOWN";

        String name = filename.contains(".") ? filename.substring(0, filename.lastIndexOf('.')) : filename;

        return name.trim().toUpperCase().replaceAll("[^A-Z0-9]+", "_");
    }

    private MachineOperatorDetailsDTO buildResponse(MachineOperator operator) {
        MachineOperatorDetailsDTO dto = mapToDTO(operator);

        List<VehicleDocument> docs = documentRepo.findByOperatorId(operator.getId());

        dto.setDocuments(mapDocuments(docs));
        return dto;
    }

    /* ================= MAPPERS ================= */

    private MachineOperatorDetailsDTO mapToDTO(MachineOperator operator) {
        MachineOperatorDetailsDTO dto = new MachineOperatorDetailsDTO();
        dto.setOperatorId(operator.getId());
        dto.setMachineId(operator.getMachine().getId());
        dto.setUserId(operator.getUser().getId());
        dto.setDriverName(operator.getDriverName());
        dto.setOperatorContact(operator.getOperatorContact());
        dto.setAddress(operator.getAddress());
        dto.setLicenseIssueDate(operator.getLicenseIssueDate());
        dto.setCreatedAt(operator.getCreatedAt());
        return dto;
    }

    private List<OperatorDocumentDTO> mapDocuments(List<VehicleDocument> docs) {
        return docs
            .stream()
            .map(doc -> {
                OperatorDocumentDTO dto = new OperatorDocumentDTO();
                dto.setDocumentId(doc.getId());
                dto.setDocType(doc.getDocType());
                dto.setUrl(doc.getFileKey());
                return dto;
            })
            .toList();
    }

    /* ================= HELPERS ================= */

    private Machine getMachine(Long id) {
        return machineRepo.findById(id).orElseThrow(() -> new NotFoundAlertException("Machine not found", "Machine", "MachineNotFound"));
    }

    private User getUser(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new NotFoundAlertException("User not found", "User", "UserNotFound"));
    }

    private void validateNewAssignment(Long machineId, Long userId) {
        if (operatorRepo.existsByMachineIdAndActiveTrue(machineId)) {
            throw new BadRequestAlertException("Machine already has operator", "MachineOperator", "Machine already has operator");
        }
        if (operatorRepo.existsByUserIdAndActiveTrue(userId)) {
            throw new BadRequestAlertException("Operator already assigned", "MachineOperator", "Operator already assigned");
        }
    }

    private void deactivateExistingAssignments(Long machineId, Long userId) {
        operatorRepo.findByMachineIdAndActiveTrue(machineId).ifPresent(op -> op.setActive(false));
        operatorRepo.findByUserIdAndActiveTrue(userId).ifPresent(op -> op.setActive(false));
    }
}

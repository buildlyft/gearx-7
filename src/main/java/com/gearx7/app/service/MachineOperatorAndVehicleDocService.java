package com.gearx7.app.service;

import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.MachineOperator;
import com.gearx7.app.domain.User;
import com.gearx7.app.domain.VehicleDocument;
import com.gearx7.app.repository.MachineOperatorRepository;
import com.gearx7.app.repository.MachineRepository;
import com.gearx7.app.repository.UserRepository;
import com.gearx7.app.repository.VehicleDocumentRepository;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MachineOperatorAndVehicleDocService {

    private final Logger log = LoggerFactory.getLogger(MachineOperatorAndVehicleDocService.class);

    private final MachineOperatorRepository machineOperatorRepository;

    private final MachineRepository machineRepository;

    private final UserRepository userRepository;

    private final VehicleDocumentRepository vehicleDocumentRepository;

    private FileStorageService fileStorageService;

    public MachineOperatorAndVehicleDocService(
        MachineOperatorRepository machineOperatorRepository,
        MachineRepository machineRepository,
        UserRepository userRepository,
        VehicleDocumentRepository vehicleDocumentRepository,
        FileStorageService fileStorageService
    ) {
        this.machineOperatorRepository = machineOperatorRepository;
        this.machineRepository = machineRepository;
        this.userRepository = userRepository;
        this.vehicleDocumentRepository = vehicleDocumentRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Assign Operator to Machine
     *
     * @param machineId
     * @param userId
     * @param vehicleDocumentId
     *
     * @return MachineOperator
     *
     */

    @Transactional
    public MachineOperator addOperator(
        String driverName,
        Long machineId,
        Long userId,
        Long vehicleDocumentId,
        String operatorContact,
        LocalDate licenseIssueDate
    ) {
        log.info(
            "SERVICE: Assigning operator | driverName={} machineId={} userId={} documentId={} operatorContact={} licenseIssueDate={}",
            driverName,
            machineId,
            userId,
            vehicleDocumentId,
            operatorContact,
            licenseIssueDate
        );
        // find given machine exists or not
        Machine machine = getMachine(machineId);

        if (machineOperatorRepository.existsByMachineId(machineId)) {
            throw new BadRequestAlertException("Machine already has an operator", "machineOperator", "operatorAlreadyAssigned");
        }
        // find given user exists or not
        User user = getUser(userId);

        VehicleDocument doc = getDocument(vehicleDocumentId);

        if (!doc.getMachine().getId().equals(machineId)) {
            throw new BadRequestAlertException(
                "Vehicle document does not belong to this machine",
                "vehicleDocument",
                "invalidMachineDocument"
            );
        }

        MachineOperator op = new MachineOperator();
        op.setDriverName(driverName);
        op.setMachine(machine);
        op.setUser(user);
        op.setVehicleDocument(doc);
        op.setOperatorContact(operatorContact);
        op.setLicenseIssueDate(licenseIssueDate);

        MachineOperator saved = machineOperatorRepository.save(op);
        log.info(
            "SERVICE SUCCESS: Operator assigned | operatorId={} driverName={} machineId={} userId={} operatorContact={} licenseIssueDate={}",
            saved.getId(),
            driverName,
            machineId,
            userId,
            operatorContact,
            licenseIssueDate
        );
        return saved;
    }

    private Machine getMachine(Long id) {
        return machineRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Machine not found with id " + id, "machine", "machineNotFound"));
    }

    private User getUser(Long id) {
        return userRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("User not found with id " + id, "user", "userNotFound"));
    }

    private VehicleDocument getDocument(Long id) {
        return vehicleDocumentRepository
            .findById(id)
            .orElseThrow(() ->
                new BadRequestAlertException("Vehicle document not found with id " + id, "vehicleDocument", "documentNotFound")
            );
    }

    /**
     * Add Vehicle Document to Machine
     *
     * @param machineId
     * @param uploadedBy
     * @param docType
     * @param file
     * @return VehicleDocument
     *
     */

    @Transactional
    public VehicleDocument addVehicleDocument(Long machineId, Long uploadedBy, String docType, MultipartFile file) {
        log.info("REQUEST: Uploading document | machineId={} uploadedBy={} docType={}", machineId, uploadedBy, docType);

        if (file == null || file.isEmpty()) {
            throw new BadRequestAlertException("File is required", "vehicleDocument", "fileMissing");
        }

        Machine machine = getMachine(machineId);

        User uploader = getUser(uploadedBy);

        // ---------- Ensure Machine Has No Existing Document ----------
        ensureMachineHasNoDocument(machineId);

        // ---------- Upload File ----------
        String key = fileStorageService.store(file);
        log.info("File uploaded successfully | storageKey={}", key);

        VehicleDocument vehicleDocument = new VehicleDocument();

        vehicleDocument.setMachine(machine);
        vehicleDocument.setUploadedBy(uploader);
        vehicleDocument.setDocType(docType);
        vehicleDocument.setFileKey(key);
        vehicleDocument.setFileName(file.getOriginalFilename());
        vehicleDocument.setContentType(file.getContentType());
        vehicleDocument.setSize(file.getSize());
        vehicleDocument.setUploadedAt(Instant.now());

        VehicleDocument saved = vehicleDocumentRepository.save(vehicleDocument);

        log.info(
            "SUCCESS: Vehicle document stored | id={} machineId={} uploadedBy={} docType={}",
            saved.getId(),
            machineId,
            uploadedBy,
            docType
        );

        return saved;
    }

    private void ensureMachineHasNoDocument(Long machineId) {
        if (vehicleDocumentRepository.existsByMachineId(machineId)) {
            throw new BadRequestAlertException("Vehicle document already exists for this machine", "vehicleDocument", "alreadyExists");
        }
    }
}

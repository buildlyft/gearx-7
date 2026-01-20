package com.gearx7.app.service;

import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.MachineOperator;
import com.gearx7.app.domain.User;
import com.gearx7.app.domain.VehicleDocument;
import com.gearx7.app.repository.MachineOperatorRepository;
import com.gearx7.app.repository.MachineRepository;
import com.gearx7.app.repository.UserRepository;
import com.gearx7.app.repository.VehicleDocumentRepository;
import com.gearx7.app.security.AuthoritiesConstants;
import com.gearx7.app.security.SecurityUtils;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

    private final FileStorageService fileStorageService;

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

    /**
     * Add Vehicle Document to Machine
     *
     * @param machineId
     * @param uploadedBy
     * @param docType
     * @param files
     * @return VehicleDocument
     *
     */

    @Transactional
    public List<VehicleDocument> addVehicleDocuments(Long machineId, Long uploadedBy, String docType, MultipartFile[] files) {
        //only ADMIN and PARTNER can upload documents
        if (!SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ADMIN, AuthoritiesConstants.PARTNER)) {
            log.warn("SERVICE SECURITY VIOLATION  Unauthorized document upload attempt | machineId={}", machineId);
            throw new AccessDeniedException("Only ADMIN and PARTNER can upload vehicle documents");
        }
        log.info(
            "SERVICE START Bulk document upload | machineId={} uploadedBy={} docType={} totalFiles={}",
            machineId,
            uploadedBy,
            docType,
            files.length
        );
        Machine machine = getMachine(machineId);
        User uploader = getUser(uploadedBy);

        //        if (vehicleDocumentRepository.existsByMachineIdAndDocType(machineId, docType)) {
        //            log.warn(
        //                "SERVICE VALIDATION FAILED  Duplicate docType | machineId={} docType={}", machineId, docType
        //            );
        //
        //            throw new BadRequestAlertException("Document of this type already exists for this machine", "vehicleDocument", "duplicateDocType"
        //            );
        //        }

        List<VehicleDocument> savedDocs = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                log.warn("SERVICE SKIP  Empty file detected | machineId={}", machineId);
                continue; // skip empty files
            }
            log.debug(
                "SERVICE PROCESSING FILE  name={} size={} contentType={}",
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType()
            );

            String key = fileStorageService.store(file);

            VehicleDocument doc = new VehicleDocument();
            doc.setMachine(machine);
            doc.setUploadedBy(uploader);
            doc.setDocType(docType);
            doc.setFileKey(key);
            doc.setFileName(file.getOriginalFilename());
            doc.setContentType(file.getContentType());
            doc.setSize(file.getSize());
            doc.setUploadedAt(Instant.now());

            savedDocs.add(vehicleDocumentRepository.save(doc));
        }
        log.info("SERVICE SUCCESS  Bulk upload completed | machineId={} documentsSaved={}", machineId, savedDocs.size());

        return savedDocs;
    }

    private Machine getMachine(Long id) {
        return machineRepository
            .findById(id)
            .orElseThrow(() -> {
                log.error("SERVICE ERROR  Machine not found | machineId={}", id);
                return new BadRequestAlertException("Machine not found with id " + id, "machine", "machineNotFound");
            });
    }

    private User getUser(Long id) {
        return userRepository
            .findById(id)
            .orElseThrow(() -> {
                log.error("SERVICE ERROR  User not found | userId={}", id);
                return new BadRequestAlertException("User not found with id " + id, "user", "userNotFound");
            });
    }

    private VehicleDocument getDocument(Long id) {
        return vehicleDocumentRepository
            .findById(id)
            .orElseThrow(() -> {
                log.error("SERVICE ERROR  Vehicle document not found | documentId={}", id);
                return new BadRequestAlertException("Vehicle document not found with id " + id, "vehicleDocument", "documentNotFound");
            });
    }
}

package com.gearx7.app.web.rest;

import com.gearx7.app.domain.VehicleDocument;
import com.gearx7.app.repository.VehicleDocumentRepository;
import com.gearx7.app.service.MachineOperatorAndVehicleDocService;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/vehicle-documents")
public class VehicleDocumentResource {

    private final Logger log = LoggerFactory.getLogger(VehicleDocumentResource.class);

    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final MachineOperatorAndVehicleDocService machineOperatorAndVehicleDocService;

    public VehicleDocumentResource(
        VehicleDocumentRepository vehicleDocumentRepository,
        MachineOperatorAndVehicleDocService machineOperatorAndVehicleDocService
    ) {
        this.vehicleDocumentRepository = vehicleDocumentRepository;
        this.machineOperatorAndVehicleDocService = machineOperatorAndVehicleDocService;
    }

    // Upload vehicle document
    @PostMapping("/bulk-upload")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<List<VehicleDocument>> attacheMultipleVehicleDocuments(
        @RequestParam Long machineId,
        @RequestParam Long uploadedBy,
        @RequestParam(required = false) String docType,
        @RequestParam("files") MultipartFile[] files
    ) {
        log.info(
            "REST REQUEST Bulk upload documents | machineId={} uploadedBy={} docType={} fileCount={}",
            machineId,
            uploadedBy,
            docType,
            files != null ? files.length : 0
        );

        if (files == null || files.length == 0) {
            log.warn("REST VALIDATION FAILED No files provided | machineId={}", machineId);
            throw new BadRequestAlertException("At least one file is required", "vehicleDocument", "filesMissing");
        }

        List<VehicleDocument> vehicleDocuments = machineOperatorAndVehicleDocService.addVehicleDocuments(
            machineId,
            uploadedBy,
            docType,
            files
        );
        log.info("REST SUCCESS  Bulk upload completed | machineId={} documentsSaved={}", machineId, vehicleDocuments.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleDocuments);
    }

    // Get docs of the machine
    @GetMapping("/getMachine-docu/{machineId}")
    public ResponseEntity<List<VehicleDocument>> getDocs(@PathVariable Long machineId) {
        log.info("REST REQUEST to Get documents of machine {}", machineId);

        List<VehicleDocument> docs = vehicleDocumentRepository.findAllByMachineId(machineId);

        if (docs.isEmpty()) {
            log.warn("REST RESULT No documents found | machineId={}", machineId);
            throw new BadRequestAlertException("No documents found for machineId " + machineId, "vehicleDocument", "documentsNotFound");
        }
        log.info("REST SUCCESS Documents fetched | machineId={} count={}", machineId, docs.size());

        return ResponseEntity.ok(docs);
    }

    @GetMapping("/getAll-docs")
    public ResponseEntity<List<VehicleDocument>> getAllDocs() {
        log.info("REST REQUEST to Get all vehicle documents");
        List<VehicleDocument> docs = vehicleDocumentRepository.findAll();
        log.info("REST REQUEST SUCCESS: {} total documents found", docs.size());
        return ResponseEntity.ok(docs);
    }
}

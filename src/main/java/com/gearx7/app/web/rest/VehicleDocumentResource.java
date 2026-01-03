package com.gearx7.app.web.rest;

import com.gearx7.app.domain.VehicleDocument;
import com.gearx7.app.repository.VehicleDocumentRepository;
import com.gearx7.app.service.MachineOperatorAndVehicleDocService;
import com.gearx7.app.service.MachineService;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/vehicle-documents")
public class VehicleDocumentResource {

    private final Logger log = LoggerFactory.getLogger(VehicleDocumentResource.class);

    private final MachineService machineService;
    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final MachineOperatorAndVehicleDocService machineOperatorAndVehicleDocService;

    public VehicleDocumentResource(
        MachineService machineService,
        VehicleDocumentRepository vehicleDocumentRepository,
        MachineOperatorAndVehicleDocService machineOperatorAndVehicleDocService
    ) {
        this.machineService = machineService;
        this.vehicleDocumentRepository = vehicleDocumentRepository;
        this.machineOperatorAndVehicleDocService = machineOperatorAndVehicleDocService;
    }

    // Upload vehicle document
    @PostMapping("")
    public ResponseEntity<VehicleDocument> createVehicleDoc(
        @RequestParam Long machineId,
        @RequestParam Long uploadedBy,
        @RequestParam String docType,
        @RequestParam MultipartFile file
    ) {
        log.info(
            "REST Request to Upload Vehicle Document | machineId={} uploadedBy={} docType={} fileReceived={}",
            machineId,
            uploadedBy,
            docType,
            file != null
        );

        VehicleDocument vehicleDocument = machineOperatorAndVehicleDocService.addVehicleDocument(machineId, uploadedBy, docType, file);

        log.info("REST Request SUCCESS: VehicleDocument created | id={} machineId={}", vehicleDocument.getId(), machineId);

        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleDocument);
    }

    // Get docs of the machine
    @GetMapping("/getMachine-docu/{machineId}")
    public ResponseEntity<VehicleDocument> getDocs(@PathVariable Long machineId) {
        log.info("REST REQUEST to Get documents of machine {}", machineId);

        VehicleDocument doc = vehicleDocumentRepository
            .findByMachineId(machineId)
            .orElseThrow(() ->
                new BadRequestAlertException("Vehicle document not found for machineId " + machineId, "vehicleDocument", "documentNotFound")
            );

        return ResponseEntity.ok(doc);
    }

    @GetMapping("/getAll-docs")
    public ResponseEntity<List<VehicleDocument>> getAllDocs() {
        log.info("REST REQUEST to Get all vehicle documents");
        List<VehicleDocument> docs = vehicleDocumentRepository.findAll();
        log.info("REST REQUEST SUCCESS: {} total documents found", docs.size());
        return ResponseEntity.ok(docs);
    }
}

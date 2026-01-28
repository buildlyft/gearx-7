package com.gearx7.app.web.rest;

import com.gearx7.app.service.dto.VehicleDocumentDTO;
import com.gearx7.app.service.dto.VehicleDocumentResponseDTO;
import com.gearx7.app.service.interfaces.VehicleDocService;
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

    private final VehicleDocService vehicleDocService;

    public VehicleDocumentResource(VehicleDocService vehicleDocService) {
        this.vehicleDocService = vehicleDocService;
    }

    // Upload documents
    @PostMapping("/bulk-upload")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<VehicleDocumentResponseDTO> upload(
        @RequestParam Long machineId,
        @RequestParam(required = false) Long uploadedBy,
        @RequestParam MultipartFile[] files
    ) {
        log.info(
            "REST REQUEST | Upload documents | machineId={} uploadedBy={} fileCount={}",
            machineId,
            uploadedBy,
            files != null ? files.length : 0
        );

        if (files == null || files.length == 0) {
            log.warn("REST VALIDATION FAILED | No files provided | machineId={}", machineId);
            return ResponseEntity.badRequest().build();
        }

        VehicleDocumentResponseDTO response = vehicleDocService.uploadDocuments(machineId, uploadedBy, files);

        log.info("REST RESPONSE | Upload success | machineId={}", machineId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get machine docs
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<VehicleDocumentResponseDTO> getMachineDocs(@PathVariable Long machineId) {
        log.info("REST REQUEST | Get machine documents | machineId={}", machineId);

        return ResponseEntity.ok(vehicleDocService.getMachineDocuments(machineId));
    }

    // Get all docs
    @GetMapping("/all")
    public ResponseEntity<List<VehicleDocumentResponseDTO>> getAllDocs() {
        log.info("REST REQUEST | Get all documents");
        return ResponseEntity.ok(vehicleDocService.getAllDocuments());
    }
}

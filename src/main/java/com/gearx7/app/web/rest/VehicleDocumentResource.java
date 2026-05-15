package com.gearx7.app.web.rest;

import com.gearx7.app.service.dto.ApiResponse;
import com.gearx7.app.service.dto.VehicleDocumentDTO;
import com.gearx7.app.service.dto.VehicleDocumentResponseDTO;
import com.gearx7.app.service.interfaces.VehicleDocService;
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

    private final VehicleDocService vehicleDocService;

    public VehicleDocumentResource(VehicleDocService vehicleDocService) {
        this.vehicleDocService = vehicleDocService;
    }

    // Upload documents
    @PostMapping("/bulk-upload")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<ApiResponse<VehicleDocumentResponseDTO>> upload(
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

            throw new BadRequestAlertException("Please upload at least one document", "vehicleDocument", "filesMissing");
        }

        VehicleDocumentResponseDTO response = vehicleDocService.uploadDocuments(machineId, uploadedBy, files);

        log.info("REST RESPONSE | Upload success | machineId={}", machineId);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, 201, "Vehicle documents uploaded successfully for machine id: " + machineId, response));
    }

    // Get machine docs
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<ApiResponse<VehicleDocumentResponseDTO>> getMachineDocs(@PathVariable Long machineId) {
        log.info("REST REQUEST | Get machine documents | machineId={}", machineId);

        VehicleDocumentResponseDTO response = vehicleDocService.getMachineDocuments(machineId);

        return ResponseEntity.ok(
            new ApiResponse<>(
                true,
                200,
                response.getDocuments().isEmpty() ? "No vehicle documents available" : "Vehicle documents fetched successfully",
                response
            )
        );
    }

    // Get all docs
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<VehicleDocumentResponseDTO>>> getAllDocs() {
        log.info("REST REQUEST | Get all documents");
        List<VehicleDocumentResponseDTO> response = vehicleDocService.getAllDocuments();

        return ResponseEntity.ok(
            new ApiResponse<>(
                true,
                200,
                response.isEmpty() ? "No vehicle documents available" : "Vehicle documents fetched successfully",
                response
            )
        );
    }

    // ================= GET SINGLE DOCUMENT =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleDocumentDTO>> getDocument(@PathVariable Long id) {
        log.info("REST REQUEST | Get document by id | id={}", id);

        VehicleDocumentDTO dto = vehicleDocService.getDocumentById(id);

        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Vehicle document fetched successfully", dto));
    }

    // ================= DELETE DOCUMENT =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<ApiResponse<?>> deleteDocument(@PathVariable Long id) {
        log.info("REST REQUEST | Delete document | id={}", id);

        vehicleDocService.deleteDocument(id);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Vehicle document deleted successfully", null));
    }
}

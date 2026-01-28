package com.gearx7.app.web.rest;

import com.gearx7.app.service.dto.MachineOperatorDetailsDTO;
import com.gearx7.app.service.interfaces.MachineOperatorService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/machine-operators")
public class MachineOperatorResource {

    private static final Logger log = LoggerFactory.getLogger(MachineOperatorResource.class);

    private final MachineOperatorService service;

    public MachineOperatorResource(MachineOperatorService service) {
        this.service = service;
    }

    /**
     *
     * @param dto MachineOperatorDetailsDTO
     *
     * consumes = request Content-Type
     * This API only accepts requests whose Content-Type is multipart/form-data
     *
     * @return MachineOperatorDetailsDTO
     */
    @PostMapping(value = "/create_and_assign", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MachineOperatorDetailsDTO> createOperatorAndAssignToMachine(
        @ModelAttribute MachineOperatorDetailsDTO dto,
        @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        log.info(
            "REST CREATE operator | machineId={} userId={} files={}",
            dto.getMachineId(),
            dto.getUserId(),
            files != null ? files.size() : 0
        );

        MachineOperatorDetailsDTO result = service.create(dto, files);

        log.info("REST Operator created successfully | operatorId={} machineId={}", result.getOperatorId(), result.getMachineId());

        return ResponseEntity.ok(result);
    }

    /**
     *
     * @param machineId
     * @return returns only the active operator for a machine otherwise you will get exception
     */
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<MachineOperatorDetailsDTO> getMachineOperatorDetails(@PathVariable Long machineId) {
        log.debug("REST request to GET active operator for machineId={}", machineId);

        return ResponseEntity.ok(service.getByMachineId(machineId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<MachineOperatorDetailsDTO>> getAllActiveMachineOperators() {
        log.debug("REST request to GET all active machine operators");

        // Assuming the service has a method to get all active machine operators
        List<MachineOperatorDetailsDTO> activeOperators = service.getAllActiveOperators();

        log.debug("REST response | activeOperatorsCount={}", activeOperators.size());

        return ResponseEntity.ok(activeOperators);
    }

    /**
     *
     * @param machineId
     * @param dto MachineOperatorDetailsDTO
     * @return new updated MachineOperatorDetailsDTO
     */
    @PutMapping(value = "/machine/{machineId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MachineOperatorDetailsDTO> reassign(
        @PathVariable Long machineId,
        @ModelAttribute MachineOperatorDetailsDTO dto,
        @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        log.info(
            "REST REASSIGN operator request | machineId={} userId={} files={}",
            machineId,
            dto.getUserId(),
            files != null ? files.size() : 0
        );
        return ResponseEntity.ok(service.reassign(machineId, dto, files));
    }
}

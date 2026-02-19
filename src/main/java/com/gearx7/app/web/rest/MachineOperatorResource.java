package com.gearx7.app.web.rest;

import com.gearx7.app.repository.MachineOperatorRepository;
import com.gearx7.app.service.dto.MachineOperatorDetailsDTO;
import com.gearx7.app.service.interfaces.MachineOperatorService;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.web.util.HeaderUtil;

@RestController
@RequestMapping("/api/machine-operators")
public class MachineOperatorResource {

    private static final Logger log = LoggerFactory.getLogger(MachineOperatorResource.class);

    private final MachineOperatorService service;

    private MachineOperatorRepository machineOperatorRepository;

    private static final String ENTITY_NAME = "machineOperator";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public MachineOperatorResource(MachineOperatorService service, MachineOperatorRepository machineOperatorRepository) {
        this.service = service;
        this.machineOperatorRepository = machineOperatorRepository;
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
        @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        log.info("REST CREATE operator | machineId={} filePresent={}", dto.getMachineId(), file != null && !file.isEmpty());

        MachineOperatorDetailsDTO result = service.create(dto, file);

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
        @RequestPart(value = "files", required = false) MultipartFile file
    ) {
        log.info("REST REASSIGN operator | machineId={} filePresent={}", machineId, file != null && !file.isEmpty());

        return ResponseEntity.ok(service.reassign(machineId, dto, file));
    }

    /**
     *
     * @param id operatorId
     * @return 204 No Content if deleted successfully
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMachineOperator(@PathVariable("id") Long id) {
        log.info("REST request to delete MachineOperator : {}", id);

        if (id == null) {
            log.error("Delete failed. Operator ID is null");
            throw new BadRequestAlertException("Invalid operatorId", ENTITY_NAME, "idnull");
        }

        if (!machineOperatorRepository.existsById(id)) {
            log.warn("MachineOperator not found with ID : {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        service.delete(id);

        log.info("MachineOperator deleted successfully with ID : {}", id);

        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

package com.gearx7.app.web.rest;

import com.gearx7.app.repository.MachineOperatorRepository;
import com.gearx7.app.service.dto.MachineOperatorDetailsDTO;
import com.gearx7.app.service.interfaces.MachineOperatorService;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.web.util.HeaderUtil;

@RestController
@RequestMapping("/api/machine-operators")
public class MachineOperatorResource {

    private static final Logger log = LoggerFactory.getLogger(MachineOperatorResource.class);

    private final MachineOperatorService machineOperatorService;

    private final MachineOperatorRepository machineOperatorRepository;

    private static final String ENTITY_NAME = "machineOperator";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public MachineOperatorResource(MachineOperatorService machineOperatorService, MachineOperatorRepository machineOperatorRepository) {
        this.machineOperatorService = machineOperatorService;
        this.machineOperatorRepository = machineOperatorRepository;
    }

    /**
     *
     * @param dto MachineOperatorDetailsDTO
     * @param photo operator's photo (required)
     * @param license operator's license (required)
     *
     * consumes = request Content-Type
     * This API only accepts requests whose Content-Type is multipart/form-data
     *
     * @return MachineOperatorDetailsDTO
     */
    @PostMapping(value = "/create_and_assign", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')") // Only admin and partner can create and assign operators
    public ResponseEntity<MachineOperatorDetailsDTO> createOperatorAndAssignToMachine(
        @ModelAttribute MachineOperatorDetailsDTO dto,
        @RequestPart(value = "photo", required = true) MultipartFile photo,
        @RequestPart(value = "license", required = true) MultipartFile license
    ) {
        log.info(
            "REST CREATE Operator | machineId={} | photoPresent={} | licensePresent={}",
            dto.getMachineId(),
            photo != null && !photo.isEmpty(),
            license != null && !license.isEmpty()
        );

        MachineOperatorDetailsDTO result = machineOperatorService.create(dto, photo, license);

        log.info("REST Operator created successfully | operatorId={} machineId={}", result.getOperatorId(), result.getMachineId());

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     *
     * @param machineId
     * @return returns only the active operator for a machine otherwise you will get exception
     */
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<MachineOperatorDetailsDTO> getMachineOperatorDetails(@PathVariable Long machineId) {
        log.debug("REST request to GET active operator for machineId={}", machineId);

        return ResponseEntity.ok(machineOperatorService.getByMachineId(machineId));
    }

    /**
     *
     * @return list of all active machine operators
     */

    @GetMapping("/active")
    public ResponseEntity<List<MachineOperatorDetailsDTO>> getAllActiveMachineOperators() {
        log.debug("REST request to GET all active machine operators");

        // Assuming the service has a method to get all active machine operators
        List<MachineOperatorDetailsDTO> activeOperators = machineOperatorService.getAllActiveOperators();

        log.debug("REST response | activeOperatorsCount={}", activeOperators.size());

        return ResponseEntity.ok(activeOperators);
    }

    /**
     *
     * @param machineId
     * @param dto MachineOperatorDetailsDTO
     * @return new updated MachineOperatorDetailsDTO
     */
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')") // Only admin and partner can reassign operators
    public ResponseEntity<MachineOperatorDetailsDTO> upsertOperator(
        @RequestPart(value = "machineId", required = false) Long machineId,
        @ModelAttribute MachineOperatorDetailsDTO dto,
        @RequestPart(value = "photo", required = false) MultipartFile photo,
        @RequestPart(value = "license", required = false) MultipartFile license
    ) {
        log.info(
            "REST UPSERT Operator START | operatorId={} | machineId={} | photoUpdate={} | licenseUpdate={}",
            dto.getOperatorId(),
            machineId,
            photo != null && !photo.isEmpty(),
            license != null && !license.isEmpty()
        );

        MachineOperatorDetailsDTO result = machineOperatorService.reassign(machineId, dto, photo, license);

        log.info("REST UPSERT Operator SUCCESS | operatorId={} | machineId={}", result.getOperatorId(), result.getMachineId());

        return ResponseEntity.ok(result);
    }

    @PatchMapping(value = "/{operatorId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')") // Only admin and partner can update operators
    public ResponseEntity<MachineOperatorDetailsDTO> partialUpdateOperator(
        @PathVariable Long operatorId,
        @ModelAttribute MachineOperatorDetailsDTO dto,
        @RequestPart(value = "photo", required = false) MultipartFile photo,
        @RequestPart(value = "license", required = false) MultipartFile license
    ) {
        log.info(
            "REST PATCH MachineOperator START | operatorId={} | photoUpdate={} | licenseUpdate={}",
            operatorId,
            photo != null && !photo.isEmpty(),
            license != null && !license.isEmpty()
        );

        MachineOperatorDetailsDTO result = machineOperatorService.partialUpdate(operatorId, dto, photo, license);

        log.info("REST PATCH MachineOperator SUCCESS | operatorId={}", operatorId);

        return ResponseEntity.ok(result);
    }

    /**
     *
     * @param  operatorId
     * @return 204 No Content if deleted successfully
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')") // Only admin and partner can delete operators
    public ResponseEntity<Void> deleteMachineOperator(@PathVariable("id") Long operatorId) {
        log.info("REST request to delete MachineOperator : {}", operatorId);

        if (operatorId == null) {
            log.error("Delete failed. Operator ID is null");
            throw new BadRequestAlertException("Invalid operatorId", ENTITY_NAME, "idnull");
        }

        machineOperatorService.delete(operatorId);

        log.info("MachineOperator deleted successfully with ID : {}", operatorId);

        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, operatorId.toString()))
            .build();
    }

    /**
     *
     * @return list of all machine operators (active and inactive)
     */

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<List<MachineOperatorDetailsDTO>> getAllOperators() {
        log.info("REST Request to get all machine operators");

        List<MachineOperatorDetailsDTO> operators = machineOperatorService.getAllOperators();

        log.info("REST GET ALL Operators SUCCESS | count={}", operators.size());

        return ResponseEntity.ok(operators);
    }
}

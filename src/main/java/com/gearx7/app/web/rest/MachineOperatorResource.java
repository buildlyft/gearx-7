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

    private static final String ENTITY_NAME = "machineOperator";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public MachineOperatorResource(MachineOperatorService machineOperatorService) {
        this.machineOperatorService = machineOperatorService;
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
    public ResponseEntity<MachineOperatorDetailsDTO> createOperator(
        @ModelAttribute MachineOperatorDetailsDTO dto,
        @RequestPart(value = "photo", required = true) MultipartFile photo,
        @RequestPart(value = "license", required = true) MultipartFile license
    ) {
        log.info(
            "REST CREATE Operator | photoPresent={} | licensePresent={}",
            photo != null && !photo.isEmpty(),
            license != null && !license.isEmpty()
        );

        MachineOperatorDetailsDTO result = machineOperatorService.create(dto, photo, license);

        log.info("REST Operator created successfully | operatorId={}", result.getOperatorId());

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping(value = "/{operatorId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<MachineOperatorDetailsDTO> partialUpdateOperator(
        @PathVariable Long operatorId,
        @ModelAttribute MachineOperatorDetailsDTO dto,
        @RequestPart(value = "photo", required = false) MultipartFile photo,
        @RequestPart(value = "license", required = false) MultipartFile license
    ) {
        log.info("REST PATCH Operator START | operatorId={}", operatorId);

        MachineOperatorDetailsDTO result = machineOperatorService.partialUpdate(operatorId, dto, photo, license);

        log.info("REST PATCH Operator SUCCESS | operatorId={}", operatorId);

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
        log.info("REST DELETE Operator START | operatorId={}", operatorId);
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

    /**
     *
     * @return list of machine operators associated with the current partner
     */
    @GetMapping("/partner")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<List<MachineOperatorDetailsDTO>> getOperatorsByPartner() {
        log.info("REST GET Operators by Partner START");

        List<MachineOperatorDetailsDTO> result = machineOperatorService.getAllOperatorsByPartner();

        log.info("REST GET Operators by Partner SUCCESS | count={}", result.size());

        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/{operatorId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<MachineOperatorDetailsDTO> updateOperator(
        @PathVariable Long operatorId,
        @ModelAttribute MachineOperatorDetailsDTO dto,
        @RequestPart(value = "photo", required = false) MultipartFile photo,
        @RequestPart(value = "license", required = false) MultipartFile license
    ) {
        log.info("REST PUT Operator START | operatorId={}", operatorId);

        MachineOperatorDetailsDTO result = machineOperatorService.update(operatorId, dto, photo, license);

        log.info("REST PUT Operator SUCCESS | operatorId={}", operatorId);

        return ResponseEntity.ok(result);
    }
}

package com.gearx7.app.web.rest;

import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.MachineOperator;
import com.gearx7.app.repository.MachineOperatorRepository;
import com.gearx7.app.repository.MachineRepository;
import com.gearx7.app.service.MachineOperatorAndVehicleDocService;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/machine-documents")
public class MachineOperatorResource {

    private final Logger log = LoggerFactory.getLogger(MachineOperatorResource.class);

    private final MachineOperatorRepository machineOperatorRepository;
    private final MachineOperatorAndVehicleDocService machineOperatorAndVehicleDocService;
    private final MachineRepository machineRepository;

    public MachineOperatorResource(
        MachineOperatorRepository machineOperatorRepository,
        MachineOperatorAndVehicleDocService machineOperatorAndVehicleDocService,
        MachineRepository machineRepository
    ) {
        this.machineOperatorRepository = machineOperatorRepository;
        this.machineOperatorAndVehicleDocService = machineOperatorAndVehicleDocService;
        this.machineRepository = machineRepository;
    }

    // Assign Operator to Machine
    @PostMapping("/assign")
    public ResponseEntity<MachineOperator> assignOperator(@RequestBody MachineOperator machineOperator) {
        log.info(
            "REST REQUEST: Assign operator | driverName={} machineId={} userId={} documentId={} contact={} issueDate={}",
            machineOperator.getDriverName(),
            machineOperator.getMachine() != null ? machineOperator.getMachine().getId() : null,
            machineOperator.getUser() != null ? machineOperator.getUser().getId() : null,
            machineOperator.getVehicleDocument() != null ? machineOperator.getVehicleDocument().getId() : null,
            machineOperator.getOperatorContact(),
            machineOperator.getLicenseIssueDate()
        );

        if (machineOperator.getMachine() == null || machineOperator.getMachine().getId() == null) {
            throw new BadRequestAlertException("Machine id is required", "machineOperator", "machineIdMissing");
        }

        if (machineOperator.getUser() == null || machineOperator.getUser().getId() == null) {
            throw new BadRequestAlertException("User id is required", "machineOperator", "userIdMissing");
        }

        if (machineOperator.getVehicleDocument() == null || machineOperator.getVehicleDocument().getId() == null) {
            throw new BadRequestAlertException("Vehicle Document id is required", "machineOperator", "docIdMissing");
        }

        MachineOperator saved = machineOperatorAndVehicleDocService.addOperator(
            machineOperator.getDriverName(),
            machineOperator.getMachine().getId(),
            machineOperator.getUser().getId(),
            machineOperator.getVehicleDocument().getId(),
            machineOperator.getOperatorContact(),
            machineOperator.getLicenseIssueDate()
        );

        log.info(
            "REST REQUEST SUCCESS: Operator assigned | operatorId={} driverName={} machineId={} userId={} vehicleDocumentId={} contact={} issueDate={}",
            saved.getDriverName(),
            saved.getId(),
            saved.getMachine().getId(),
            saved.getUser().getId(),
            saved.getVehicleDocument().getId(),
            saved.getOperatorContact(),
            saved.getLicenseIssueDate()
        );

        return ResponseEntity.ok(saved);
    }

    // Get operator of machine
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<?> getOperatorByMachine(@PathVariable Long machineId) {
        log.info("REST REQUEST: Get operator for machineId={}", machineId);

        Machine machine = machineRepository
            .findById(machineId)
            .orElseThrow(() -> {
                log.error("REST ERROR: Machine not found | machineId={}", machineId);
                return new BadRequestAlertException("Machine not found with id " + machineId, "machineOperator", "machineNotFound");
            });

        return machineOperatorRepository
            .findByMachineId(machineId)
            .<ResponseEntity<?>>map(operator -> {
                log.info("REST SUCCESS: Operator found | operatorId={} machineId={}", operator.getId(), machineId);
                return ResponseEntity.ok(operator);
            })
            .orElseGet(() -> {
                log.warn("REST INFO: Machine exists but no operator assigned | machineId={}", machineId);
                return ResponseEntity.ok(Map.of("message", "Machine exists but no operator assigned yet", "machineId", machineId));
            });
    }

    @GetMapping("/getAllOperators")
    public ResponseEntity<List<MachineOperator>> getAllOperators() {
        List<MachineOperator> operators = machineOperatorRepository.findAll();
        log.info("REST SUCCESS: {} operators found", operators.size());
        return ResponseEntity.ok(operators);
    }
}

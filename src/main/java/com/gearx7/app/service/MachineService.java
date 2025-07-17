package com.gearx7.app.service;

import com.gearx7.app.domain.Machine;
import com.gearx7.app.repository.MachineRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gearx7.app.domain.Machine}.
 */
@Service
@Transactional
public class MachineService {

    private final Logger log = LoggerFactory.getLogger(MachineService.class);

    private final MachineRepository machineRepository;

    public MachineService(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    /**
     * Save a machine.
     *
     * @param machine the entity to save.
     * @return the persisted entity.
     */
    public Machine save(Machine machine) {
        log.debug("Request to save Machine : {}", machine);
        return machineRepository.save(machine);
    }

    /**
     * Update a machine.
     *
     * @param machine the entity to save.
     * @return the persisted entity.
     */
    public Machine update(Machine machine) {
        log.debug("Request to update Machine : {}", machine);
        return machineRepository.save(machine);
    }

    /**
     * Partially update a machine.
     *
     * @param machine the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Machine> partialUpdate(Machine machine) {
        log.debug("Request to partially update Machine : {}", machine);

        return machineRepository
            .findById(machine.getId())
            .map(existingMachine -> {
                if (machine.getBrand() != null) {
                    existingMachine.setBrand(machine.getBrand());
                }
                if (machine.getType() != null) {
                    existingMachine.setType(machine.getType());
                }
                if (machine.getTag() != null) {
                    existingMachine.setTag(machine.getTag());
                }
                if (machine.getModel() != null) {
                    existingMachine.setModel(machine.getModel());
                }
                if (machine.getVinNumber() != null) {
                    existingMachine.setVinNumber(machine.getVinNumber());
                }
                if (machine.getChassisNumber() != null) {
                    existingMachine.setChassisNumber(machine.getChassisNumber());
                }
                if (machine.getDescription() != null) {
                    existingMachine.setDescription(machine.getDescription());
                }
                if (machine.getCapacityTon() != null) {
                    existingMachine.setCapacityTon(machine.getCapacityTon());
                }
                if (machine.getRatePerHour() != null) {
                    existingMachine.setRatePerHour(machine.getRatePerHour());
                }
                if (machine.getMinimumUsageHours() != null) {
                    existingMachine.setMinimumUsageHours(machine.getMinimumUsageHours());
                }
                if (machine.getLatitude() != null) {
                    existingMachine.setLatitude(machine.getLatitude());
                }
                if (machine.getLongitude() != null) {
                    existingMachine.setLongitude(machine.getLongitude());
                }
                if (machine.getTransportationCharge() != null) {
                    existingMachine.setTransportationCharge(machine.getTransportationCharge());
                }
                if (machine.getDriverBatta() != null) {
                    existingMachine.setDriverBatta(machine.getDriverBatta());
                }
                if (machine.getServiceabilityRangeKm() != null) {
                    existingMachine.setServiceabilityRangeKm(machine.getServiceabilityRangeKm());
                }
                if (machine.getStatus() != null) {
                    existingMachine.setStatus(machine.getStatus());
                }
                if (machine.getCreatedDate() != null) {
                    existingMachine.setCreatedDate(machine.getCreatedDate());
                }

                return existingMachine;
            })
            .map(machineRepository::save);
    }

    /**
     * Get all the machines.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Machine> findAll(Pageable pageable) {
        log.debug("Request to get all Machines");
        return machineRepository.findAll(pageable);
    }

    /**
     * Get one machine by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Machine> findOne(Long id) {
        log.debug("Request to get Machine : {}", id);
        return machineRepository.findById(id);
    }

    /**
     * Delete the machine by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Machine : {}", id);
        machineRepository.deleteById(id);
    }
}

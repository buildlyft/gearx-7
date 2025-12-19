package com.gearx7.app.service;

import com.gearx7.app.domain.Machine;
import com.gearx7.app.repository.CategoryRepository;
import com.gearx7.app.repository.MachineRepository;
import com.gearx7.app.repository.SubcategoryRepository;
import com.gearx7.app.service.dto.MachineDTO;
import com.gearx7.app.service.mapper.MachineMapper;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
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

    private final MachineMapper machineMapper;

    private final CategoryRepository categoryRepository;

    private final SubcategoryRepository subCategoryRepository;

    public MachineService(
        MachineRepository machineRepository,
        MachineMapper machineMapper,
        CategoryRepository categoryRepository,
        SubcategoryRepository subCategoryRepository
    ) {
        this.machineRepository = machineRepository;
        this.machineMapper = machineMapper;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    /**
     * Save a machine.
     *
     * @param machineDTO the entity to save.
     * @return the persisted entity.
     */
    public MachineDTO save(MachineDTO machineDTO) {
        log.debug("Request to save Machine : {}", machineDTO);
        Machine machine = machineMapper.toEntity(machineDTO);
        machine = machineRepository.save(machine);
        return machineMapper.toDto(machine);
    }

    /**
     * Update a machine.
     *
     * @param machineDTO the entity to save.
     * @return the persisted entity.
     */
    public MachineDTO update(MachineDTO machineDTO) {
        log.debug("Request to update Machine : {}", machineDTO);
        Machine machine = machineMapper.toEntity(machineDTO);
        machine = machineRepository.save(machine);
        return machineMapper.toDto(machine);
    }

    /**
     * Partially update a machine.
     *
     * @param machineDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MachineDTO> partialUpdate(MachineDTO machineDTO) {
        log.debug("Request to partially update Machine : {}", machineDTO);

        return machineRepository
            .findById(machineDTO.getId())
            .map(existingMachine -> {
                machineMapper.partialUpdate(existingMachine, machineDTO);

                return existingMachine;
            })
            .map(machineRepository::save)
            .map(machineMapper::toDto);
    }

    /**
     * Get all the machines.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MachineDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Machines");
        return machineRepository.findAll(pageable).map(machineMapper::toDto);
    }

    /**
     * Get all the machines with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MachineDTO> findAllWithEagerRelationships(Pageable pageable) {
        return machineRepository.findAllWithEagerRelationships(pageable).map(machineMapper::toDto);
    }

    /**
     * Get one machine by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MachineDTO> findOne(Long id) {
        log.debug("Request to get Machine : {}", id);
        return machineRepository.findOneWithEagerRelationships(id).map(machineMapper::toDto);
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

    /**
     * searching machines based on current user lat,lon around 10.0 KM
     *
     * @param categoryId
     * @param subcategoryId
     * @param start
     * @param end
     * @param userLat
     * @param userLon
     * @return
     */

    @Transactional(readOnly = true)
    public List<MachineDTO> searchMachines(
        Long categoryId,
        Long subcategoryId,
        Instant start,
        Instant end,
        Double userLat,
        Double userLon
    ) {
        log.info(
            "Searching machines for categoryId={}, subcategoryId={}, start={}, end={}, lat={}, lon={}",
            categoryId,
            subcategoryId,
            start,
            end,
            userLat,
            userLon
        );

        if (!categoryRepository.existsById(categoryId)) {
            log.error("Category not found with id: {}", categoryId);
            throw new BadRequestAlertException("Category not found with id " + categoryId, "category", "categoryNotFound");
        }

        if (!subCategoryRepository.existsById(subcategoryId)) {
            log.error("Subcategory not found: {}", subcategoryId);
            throw new BadRequestAlertException("Subcategory not found with id " + subcategoryId, "subcategory", "subcategoryNotFound");
        }

        if (!subCategoryRepository.existsByIdAndCategoryId(subcategoryId, categoryId)) {
            log.error("Subcategory {} does not belong to Category {}", subcategoryId, categoryId);
            throw new BadRequestAlertException(
                "Subcategory does not belong to selected category",
                "subcategory",
                "invalidCategorySubcategory"
            );
        }

        if (userLat < -90 || userLat > 90 || userLon < -180 || userLon > 180) {
            throw new BadRequestAlertException("Invalid latitude or longitude", "machine", "invalidLocation");
        }

        final double RADIUS_KM = 10.0;

        double latOffset = RADIUS_KM / 111.0;
        double lonOffset = RADIUS_KM / (111.0 * Math.cos(Math.toRadians(userLat)));

        double minLat = userLat - latOffset;
        double maxLat = userLat + latOffset;
        double minLon = userLon - lonOffset;
        double maxLon = userLon + lonOffset;

        log.debug("Radius Box => minLat={}, maxLat={}, minLon={}, maxLon={}", minLat, maxLat, minLon, maxLon);

        List<Machine> machines = machineRepository.findAvailableMachinesWithinRadius(
            categoryId,
            subcategoryId,
            minLat,
            maxLat,
            minLon,
            maxLon
        );

        if (machines == null) {
            machines = Collections.emptyList();
        }

        log.info("{} machines found in 10 KM bounding box", machines.size());

        List<MachineDTO> availableMachines = new ArrayList<>();

        LocalDate startDate = start.atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate endDate = end.atZone(ZoneOffset.UTC).toLocalDate();
        boolean isSameDay = startDate.equals(endDate);

        for (Machine machine : machines) {
            MachineDTO dto = machineMapper.toDto(machine);

            if (isSameDay) {
                long hours = ChronoUnit.HOURS.between(start, end);
                if (hours <= 0) hours = 1;

                if (machine.getMinimumUsageHours() != null && hours < machine.getMinimumUsageHours()) {
                    hours = machine.getMinimumUsageHours();
                }

                dto.setTotalHourlyRate(machine.getRatePerHour().multiply(BigDecimal.valueOf(hours)).setScale(2, RoundingMode.HALF_UP));
                dto.setTotalDailyRate(null);
            } else {
                long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;

                BigDecimal ratePerDay = machine.getRatePerDay() != null
                    ? machine.getRatePerDay()
                    : machine.getRatePerHour().multiply(BigDecimal.valueOf(24));

                dto.setTotalDailyRate(ratePerDay.multiply(BigDecimal.valueOf(days)).setScale(2, RoundingMode.HALF_UP));
                dto.setTotalHourlyRate(null);
            }

            availableMachines.add(dto);
        }

        log.info("Total {} machines returned to controller", availableMachines.size());
        return availableMachines;
    }
}

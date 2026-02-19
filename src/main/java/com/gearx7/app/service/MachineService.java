package com.gearx7.app.service;

import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.User;
import com.gearx7.app.repository.*;
import com.gearx7.app.security.AuthoritiesConstants;
import com.gearx7.app.security.SecurityUtils;
import com.gearx7.app.service.dto.MachineDTO;
import com.gearx7.app.service.mapper.MachineMapper;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    private final UserRepository userRepository;

    private final TypeRepository typeRepository;

    public MachineService(
        MachineRepository machineRepository,
        MachineMapper machineMapper,
        CategoryRepository categoryRepository,
        SubcategoryRepository subCategoryRepository,
        UserRepository userRepository,
        TypeRepository typeRepository
    ) {
        this.machineRepository = machineRepository;
        this.machineMapper = machineMapper;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.userRepository = userRepository;
        this.typeRepository = typeRepository;
    }

    /**
     * Save a machine.
     *
     * @param machineDTO the entity to save.
     * @return the persisted entity.
     */
    @Transactional
    public MachineDTO save(MachineDTO machineDTO) {
        log.debug("Request to save Machine : {}", machineDTO);

        //  FINAL SECURITY GATE
        if (!SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ADMIN, AuthoritiesConstants.PARTNER)) {
            throw new AccessDeniedException("You are not allowed to create a machine");
        }

        validateHierarchy(machineDTO.getTypeId(), machineDTO.getCategoryId(), machineDTO.getSubcategoryId());

        Machine machine = machineMapper.toEntity(machineDTO);

        // ADMIN Flow: specify partner user
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            User partner = validateAndLoadPartner(machineDTO);
            machine.setUser(partner);
            // PARTNER Flow: assign current logged-in user
        } else if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.PARTNER)) {
            User currentUser = getCurrentLoggedInUser();
            machine.setUser(currentUser);
        }

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
        log.info("Request to get Machine : {}", id);

        Optional<MachineDTO> results = machineRepository.findById(id).map(machineMapper::toDto);
        log.info("Found Machine with id {} ", id);
        return results;
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
     * @param typeId
     * @param categoryId
     * @param subcategoryId
     * @param start
     * @param end
     * @param userLat
     * @param userLon
     * @return list of machines matching criteria
     */

    @Transactional(readOnly = true)
    public List<MachineDTO> searchMachines(
        Long typeId,
        Long categoryId,
        Long subcategoryId,
        Instant start,
        Instant end,
        Double userLat,
        Double userLon
    ) {
        final double RADIUS_KM = 15.0;

        log.info(
            "Searching within {} KM radius for typeId={}, categoryId={}, subcategoryId={}, start={}, end={}, lat={}, lon={}",
            RADIUS_KM,
            typeId,
            categoryId,
            subcategoryId,
            start,
            end,
            userLat,
            userLon
        );

        validateHierarchy(typeId, categoryId, subcategoryId);
        validateLocation(userLat, userLon);

        BoundingBox box = calculateBoundingBox(userLat, userLon, RADIUS_KM);

        List<Machine> machines = machineRepository.searchWithinRadius(
            subcategoryId,
            userLat,
            userLon,
            box.minLat,
            box.maxLat,
            box.minLon,
            box.maxLon,
            RADIUS_KM
        );

        return machines.stream().map(machine -> calculatePricing(machine, start, end)).toList();
    }

    @Transactional(readOnly = true)
    public List<MachineDTO> findMachinesWithoutOperator() {
        log.info("Request to fetch Machines without active operator");

        List<Machine> machines = machineRepository.findMachinesWithoutOperator();

        if (machines.isEmpty()) {
            log.info("No machines found without active operator");
        } else {
            log.info("Found {} machines without active operator", machines.size());
        }

        return machineMapper.toDto(machines);
    }

    private User getCurrentLoggedInUser() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No logged-in user"));

        return userRepository.findOneByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private User validateAndLoadPartner(MachineDTO machineDTO) {
        if (machineDTO.getUser() == null || machineDTO.getUser().getId() == null) {
            throw new BadRequestAlertException("Admin must specify partner user", "machine", "userrequired");
        }

        Long partnerId = machineDTO.getUser().getId();

        User partner = userRepository
            .findById(partnerId)
            .orElseThrow(() -> new BadRequestAlertException("Partner user does not exist", "machine", "usernotfound"));

        boolean isPartner = partner.getAuthorities().stream().anyMatch(a -> AuthoritiesConstants.PARTNER.equals(a.getName()));

        if (!isPartner) {
            throw new BadRequestAlertException("Machine can be created only for PARTNER users", "machine", "invalidpartner");
        }

        return partner;
    }

    private void validateHierarchy(Long typeId, Long categoryId, Long subcategoryId) {
        if (!typeRepository.existsById(typeId)) throw new BadRequestAlertException("Type not found", "type", "typeNotFound");

        if (!categoryRepository.existsByIdAndTypeId(categoryId, typeId)) throw new BadRequestAlertException(
            "Invalid Category for Type",
            "category",
            "invalidTypeCategory"
        );

        if (!subCategoryRepository.existsByIdAndCategoryId(subcategoryId, categoryId)) throw new BadRequestAlertException(
            "Invalid Subcategory for Category",
            "subcategory",
            "invalidCategorySubcategory"
        );
    }

    private void validateLocation(Double lat, Double lon) {
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) throw new BadRequestAlertException(
            "Invalid coordinates",
            "machine",
            "invalidLocation"
        );
    }

    private BoundingBox calculateBoundingBox(double lat, double lon, double radiusKm) {
        double latRadius = radiusKm / 111.0;
        double lonRadius = radiusKm / (111.0 * Math.cos(Math.toRadians(lat)));

        return new BoundingBox(lat - latRadius, lat + latRadius, lon - lonRadius, lon + lonRadius);
    }

    private record BoundingBox(double minLat, double maxLat, double minLon, double maxLon) {}

    private MachineDTO calculatePricing(Machine machine, Instant start, Instant end) {
        MachineDTO dto = machineMapper.toDto(machine);

        LocalDate startDate = start.atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate endDate = end.atZone(ZoneOffset.UTC).toLocalDate();

        boolean isSameDay = startDate.equals(endDate);

        if (isSameDay) {
            long totalSeconds = Duration.between(start, end).getSeconds();
            long hours = (long) Math.ceil(totalSeconds / 3600.0);
            hours = Math.max(1, hours);

            if (machine.getMinimumUsageHours() != null) {
                hours = Math.max(hours, machine.getMinimumUsageHours());
            }

            BigDecimal hourlyTotal = machine.getRatePerHour().multiply(BigDecimal.valueOf(hours));

            if (machine.getRatePerDay() != null && hourlyTotal.compareTo(machine.getRatePerDay()) > 0) {
                dto.setTotalDailyRate(machine.getRatePerDay().setScale(2, RoundingMode.HALF_UP));
                dto.setTotalHourlyRate(null);
            } else {
                dto.setTotalHourlyRate(hourlyTotal.setScale(2, RoundingMode.HALF_UP));
                dto.setTotalDailyRate(null);
            }
        }

        return dto;
    }
}

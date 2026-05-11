package com.gearx7.app.service;

import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.MachineOperator;
import com.gearx7.app.domain.User;
import com.gearx7.app.repository.*;
import com.gearx7.app.security.AuthoritiesConstants;
import com.gearx7.app.security.SecurityUtils;
import com.gearx7.app.service.LocationIQService;
import com.gearx7.app.service.dto.MachineDTO;
import com.gearx7.app.service.mapper.MachineMapper;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import com.gearx7.app.web.rest.errors.NotFoundAlertException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
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

    private final MachineOperatorRepository machineOperatorRepository;

    private final LocationIQService locationIQService;

    public MachineService(
        MachineRepository machineRepository,
        MachineMapper machineMapper,
        CategoryRepository categoryRepository,
        SubcategoryRepository subCategoryRepository,
        UserRepository userRepository,
        TypeRepository typeRepository,
        MachineOperatorRepository machineOperatorRepository,
        LocationIQService locationIQService
    ) {
        this.machineRepository = machineRepository;
        this.machineMapper = machineMapper;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.userRepository = userRepository;
        this.typeRepository = typeRepository;
        this.machineOperatorRepository = machineOperatorRepository;
        this.locationIQService = locationIQService;
    }

    /**
     * Save a machine.
     *
     * @param machineDTO the entity to save.
     * @return the persisted entity.
     */
    @Transactional
    public MachineDTO save(MachineDTO machineDTO) {
        log.info(
            "Start: Saving Machine. typeId={}, categoryId={}, subcategoryId={}",
            machineDTO.getTypeId(),
            machineDTO.getCategoryId(),
            machineDTO.getSubcategoryId()
        );

        //  FINAL SECURITY GATE
        if (!SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ADMIN, AuthoritiesConstants.PARTNER)) {
            log.warn("Unauthorized attempt to create machine");
            throw new AccessDeniedException("You are not allowed to create a machine");
        }

        log.debug(
            "Validating hierarchy for typeId={}, categoryId={}, subcategoryId={}",
            machineDTO.getTypeId(),
            machineDTO.getCategoryId(),
            machineDTO.getSubcategoryId()
        );

        validateHierarchy(machineDTO.getTypeId(), machineDTO.getCategoryId(), machineDTO.getSubcategoryId());

        Machine machine = machineMapper.toEntity(machineDTO);
        if (machine.getLatitude() != null && machine.getLongitude() != null) {
            String address = locationIQService.getAddress(machine.getLatitude(), machine.getLongitude());

            machine.setMachineAddress(address);
        }
        // ADMIN Flow: specify partner user
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            log.debug("ADMIN flow: assigning partner user");
            User partner = validateAndLoadPartner(machineDTO);
            machine.setUser(partner);
            // PARTNER Flow: assign current logged-in user
        } else if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.PARTNER)) {
            log.debug("PARTNER flow: assigning current logged-in user");
            User currentUser = getCurrentLoggedInUser();
            machine.setUser(currentUser);
        }

        machine = machineRepository.save(machine);
        log.info("Machine saved successfully. ID={}", machine.getId());
        return machineMapper.toDto(machine);
    }

    /**
     * Partially update a machine.
     *
     * @param machineDTO the entity to update partially.
     * @return the persisted entity.
     */
    @Transactional
    public MachineDTO partialUpdate(MachineDTO machineDTO) {
        log.debug("Request to partially update Machine : {}", machineDTO);

        Machine existingMachine = machineRepository
            .findById(machineDTO.getId())
            .orElseThrow(() -> {
                log.warn("Machine not found | id={}", machineDTO.getId());

                return new NotFoundAlertException("Machine not found with id " + machineDTO.getId(), "Machine", "machineNotFound");
            });

        String login = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new NotFoundAlertException("User not found", "User", "userNotFound"));

        // MACHINE OWNERSHIP CHECK
        boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN");

        if (!isAdmin && !existingMachine.getUser().getLogin().equals(login)) {
            log.warn("Unauthorized machine update attempt | machineId={} | login={}", existingMachine.getId(), login);

            throw new BadRequestAlertException("You are not allowed to modify this machine", "Machine", "forbidden");
        }

        // UPDATE NORMAL FIELDS
        machineMapper.partialUpdate(existingMachine, machineDTO);
        if (existingMachine.getLatitude() != null && existingMachine.getLongitude() != null) {
            String address = locationIQService.getAddress(existingMachine.getLatitude(), existingMachine.getLongitude());

            existingMachine.setMachineAddress(address);
        }

        // OPERATOR ASSIGNMENT
        if (machineDTO.getOperatorId() != null) {
            MachineOperator operator = machineOperatorRepository
                .findById(machineDTO.getOperatorId())
                .orElseThrow(() -> {
                    log.warn("Operator not found | operatorId={}", machineDTO.getOperatorId());

                    return new NotFoundAlertException("Operator not found", "MachineOperator", "operatorNotFound");
                });

            // OPERATOR OWNERSHIP CHECK
            if (!isAdmin && !operator.getPartner().getLogin().equals(login)) {
                throw new BadRequestAlertException("You are not allowed to use this operator", "MachineOperator", "forbidden");
            }

            // REMOVE OLD OPERATOR FROM MACHINE
            MachineOperator oldOperator = existingMachine.getOperator();

            if (oldOperator != null && !oldOperator.getId().equals(operator.getId())) {
                oldOperator.setMachine(null);
            }

            // REMOVE OPERATOR FROM PREVIOUS MACHINE
            Machine oldMachine = operator.getMachine();

            if (oldMachine != null && !oldMachine.getId().equals(existingMachine.getId())) {
                oldMachine.setOperator(null);

                machineRepository.saveAndFlush(oldMachine);
            }

            // ASSIGN NEW OPERATOR
            existingMachine.setOperator(operator);

            operator.setMachine(existingMachine);

            log.info("Operator assigned/replaced | machineId={} | operatorId={}", existingMachine.getId(), operator.getId());
        }

        Machine updatedMachine = machineRepository.save(existingMachine);

        log.info("Machine partially updated successfully | id={}", updatedMachine.getId());

        return machineMapper.toDto(updatedMachine);
    }

    /**
     * Update a machine.
     *
     * @param machineDTO the entity to save.
     * @return the persisted entity.
     */
    @Transactional
    public MachineDTO update(MachineDTO machineDTO) {
        log.info("PUT | Updating Machine | id={}", machineDTO.getId());

        Machine existingMachine = machineRepository
            .findById(machineDTO.getId())
            .orElseThrow(() -> {
                log.warn("Machine not found | id={}", machineDTO.getId());

                return new NotFoundAlertException("Machine not found with id " + machineDTO.getId(), "Machine", "machineNotFound");
            });

        String login = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new NotFoundAlertException("User not found", "User", "userNotFound"));

        // OWNERSHIP CHECK
        boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN");

        if (!isAdmin && !existingMachine.getUser().getLogin().equals(login)) {
            log.warn("Unauthorized update attempt | machineId={} | login={}", existingMachine.getId(), login);

            throw new BadRequestAlertException("You are not allowed to update this machine", "Machine", "forbidden");
        }

        // UPDATE NORMAL FIELDS
        machineMapper.partialUpdate(existingMachine, machineDTO);
        if (existingMachine.getLatitude() != null && existingMachine.getLongitude() != null) {
            String address = locationIQService.getAddress(existingMachine.getLatitude(), existingMachine.getLongitude());

            existingMachine.setMachineAddress(address);
        }

        // OPERATOR ASSIGNMENTs
        if (machineDTO.getOperatorId() != null) {
            MachineOperator operator = machineOperatorRepository
                .findById(machineDTO.getOperatorId())
                .orElseThrow(() -> {
                    log.warn("Operator not found | operatorId={}", machineDTO.getOperatorId());

                    return new NotFoundAlertException("Operator not found", "MachineOperator", "operatorNotFound");
                });
            // OPERATOR OWNERSHIP CHECK
            if (!isAdmin && !operator.getPartner().getLogin().equals(login)) {
                throw new BadRequestAlertException("You are not allowed to use this operator", "MachineOperator", "forbidden");
            }

            // REMOVE OPERATOR FROM OLD MACHINE
            Machine previousMachine = operator.getMachine();

            if (previousMachine != null && !previousMachine.getId().equals(existingMachine.getId())) {
                previousMachine.setOperator(null);

                machineRepository.saveAndFlush(previousMachine);
            }

            // REMOVE OLD OPERATOR FROM CURRENT MACHINE
            MachineOperator oldOperator = existingMachine.getOperator();

            if (oldOperator != null && !oldOperator.getId().equals(operator.getId())) {
                oldOperator.setMachine(null);
            }

            // ASSIGN NEW OPERATOR
            existingMachine.setOperator(operator);
            operator.setMachine(existingMachine);
        }

        Machine saved = machineRepository.save(existingMachine);

        log.info("Machine updated successfully | id={}", saved.getId());

        return machineMapper.toDto(saved);
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
    public MachineDTO findOne(Long id) {
        log.info("Request to get Machine : {}", id);

        Machine machine = machineRepository
            .findById(id)
            .orElseThrow(() -> {
                log.warn("Machine not found | id={}", id);

                return new NotFoundAlertException("Machine not found with id " + id, "Machine", "machineNotFound");
            });

        log.info("Found Machine with id {}", id);

        return machineMapper.toDto(machine);
    }

    /**
     * Delete the machine by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.info("Request to delete Machine : {}", id);
        Machine machine = machineRepository
            .findById(id)
            .orElseThrow(() -> {
                log.warn("Machine not found | id={}", id);

                return new NotFoundAlertException("Machine not found with id " + id, "Machine", "machineNotFound");
            });
        String login = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new NotFoundAlertException("User not found", "User", "userNotFound"));

        boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN");

        if (!isAdmin && !machine.getUser().getLogin().equals(login)) {
            log.warn("Unauthorized delete attempt | machineId={} | login={}", machine.getId(), login);

            throw new BadRequestAlertException("You are not allowed to delete this machine", "Machine", "forbidden");
        }

        machineRepository.delete(machine);

        log.info("Machine deleted successfully | id={}", id);
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
            log.warn("No machines found without active operator");

            throw new NotFoundAlertException("No machines available without operator", "Machine", "machinesWithoutOperatorNotFound");
        }

        log.info("Found {} machines without active operator", machines.size());

        return machineMapper.toDto(machines);
    }

    @Transactional(readOnly = true)
    public List<MachineDTO> getMachinesByOwner(Long ownerId) {
        log.debug("SERVICE | Get machines | requestOwnerId={}", ownerId);

        boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN");

        List<Machine> machines;

        if (isAdmin) {
            //  ADMIN FLOW
            if (ownerId == null) {
                log.warn("ADMIN FLOW | ownerId missing");
                throw new BadRequestAlertException("PartnerId (ownerId) is required for admin", "machine", "ownerIdMissing");
            }

            if (!userRepository.existsById(ownerId)) {
                log.warn("ADMIN FLOW | User not found | ownerId={}", ownerId);
                throw new NotFoundAlertException("User not found with id " + ownerId, "User", "UserNotFound");
            }

            log.info("ADMIN FLOW | Fetch machines for ownerId={}", ownerId);

            machines = machineRepository.findByUserId(ownerId);
        } else {
            //  PARTNER FLOW
            String login = SecurityUtils
                .getCurrentUserLogin()
                .orElseThrow(() -> {
                    log.error("JWT missing login");
                    return new NotFoundAlertException("User not found", "User", "UserNotFound");
                });

            log.info("PARTNER FLOW | login={}", login);

            machines = machineRepository.findByOwnerLogin(login);
        }

        if (machines.isEmpty()) {
            if (isAdmin) {
                log.warn("No machines found for ownerId={}", ownerId);

                throw new NotFoundAlertException("No machines found for this partner", "Machine", "machinesNotFound");
            }

            log.warn("Partner has no machines | login={}", SecurityUtils.getCurrentUserLogin().orElse("UNKNOWN"));

            throw new NotFoundAlertException("You don't have any machines", "Machine", "machinesNotFound");
        }

        log.info("Machines fetched | count={}", machines.size());

        return machineMapper.toDto(machines);
    }

    private User getCurrentLoggedInUser() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No logged-in user"));

        return userRepository.findOneByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private User validateAndLoadPartner(MachineDTO machineDTO) {
        log.debug("Validating partner user from DTO");
        if (machineDTO.getUser() == null || machineDTO.getUser().getId() == null) {
            log.error("Partner user is missing in request");
            throw new BadRequestAlertException("Admin must specify partner user", "machine", "userrequired");
        }

        Long partnerId = machineDTO.getUser().getId();
        log.debug("Fetching partner user with ID: {}", partnerId);

        User partner = userRepository
            .findById(partnerId)
            .orElseThrow(() -> {
                log.error("Partner user not found with ID: {}", partnerId);
                return new BadRequestAlertException("Partner user does not exist", "machine", "usernotfound");
            });

        boolean isPartner = partner.getAuthorities().stream().anyMatch(a -> AuthoritiesConstants.PARTNER.equals(a.getName()));

        if (!isPartner) {
            log.error("User {} is not a PARTNER", partnerId);
            throw new BadRequestAlertException("Machine can be created only for PARTNER users", "machine", "invalidpartner");
        }
        log.debug("Partner validation successful for user ID: {}", partnerId);

        return partner;
    }

    private void validateHierarchy(Long typeId, Long categoryId, Long subcategoryId) {
        log.debug("Validating Type existence: {}", typeId);

        if (!typeRepository.existsById(typeId)) {
            log.error("Type not found: {}", typeId);
            throw new BadRequestAlertException("Type not found", "type", "typeNotFound");
        }
        log.debug("Validating Category {} belongs to Type {}", categoryId, typeId);
        if (!categoryRepository.existsByIdAndTypeId(categoryId, typeId)) {
            log.error("Invalid Category {} for Type {}", categoryId, typeId);
            throw new BadRequestAlertException("Invalid Category for Type", "category", "invalidTypeCategory");
        }

        log.debug("Validating Subcategory {} belongs to Category {}", subcategoryId, categoryId);
        if (!subCategoryRepository.existsByIdAndCategoryId(subcategoryId, categoryId)) {
            log.error("Invalid Subcategory {} for Category {}", subcategoryId, categoryId);
            throw new BadRequestAlertException("Invalid Subcategory for Category", "subcategory", "invalidCategorySubcategory");
        }

        log.debug("Hierarchy validation passed");
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
        log.info("PRICING START | machineId={} | start={} | end={}", machine.getId(), start, end);

        MachineDTO dto = machineMapper.toDto(machine);

        if (start == null || end == null || end.isBefore(start)) {
            log.error("Invalid booking time | start={} | end={}", start, end);
            throw new IllegalArgumentException("Invalid booking time");
        }

        ZoneId IST = ZoneId.of("Asia/Kolkata");

        LocalDate startDate = start.atZone(IST).toLocalDate();
        LocalDate endDate = end.atZone(IST).toLocalDate();
        long totalSeconds = Duration.between(start, end).getSeconds();
        long totalHours = (long) Math.ceil(totalSeconds / 3600.0);

        log.debug("Duration calculated | seconds={} | hours(beforeMin)={}", totalSeconds, totalHours);

        totalHours = Math.max(1, totalHours);

        if (machine.getMinimumUsageHours() != null) {
            long beforeMin = totalHours;
            totalHours = Math.max(totalHours, machine.getMinimumUsageHours());

            log.debug("Minimum usage applied | before={} | min={} | after={}", beforeMin, machine.getMinimumUsageHours(), totalHours);
        }

        // ==============================
        // SAME DAY
        // ==============================
        if (startDate.equals(endDate)) {
            BigDecimal hourlyTotal = machine.getRatePerHour().multiply(BigDecimal.valueOf(totalHours));

            log.debug("Same day booking | hours={} | hourlyTotal={}", totalHours, hourlyTotal);

            if (machine.getRatePerDay() != null && hourlyTotal.compareTo(machine.getRatePerDay()) > 0) {
                log.info("Pricing decision: DAILY | dailyRate={} cheaper than hourly={}", machine.getRatePerDay(), hourlyTotal);

                dto.setTotalDailyRate(machine.getRatePerDay().setScale(2, RoundingMode.HALF_UP));
                dto.setTotalHourlyRate(null);
            } else {
                log.info("Pricing decision: HOURLY | hours={} | total={}", totalHours, hourlyTotal);

                dto.setTotalHourlyRate(hourlyTotal.setScale(2, RoundingMode.HALF_UP));
                dto.setTotalDailyRate(null);
            }

            log.info(
                "PRICING END | machineId={} | hourly={} | daily={}",
                machine.getId(),
                dto.getTotalHourlyRate(),
                dto.getTotalDailyRate()
            );

            return dto;
        }

        // ==============================
        // MULTI-DAY
        // ==============================

        long totalDays = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays();

        long remainingHours = totalHours - (totalDays * 24);

        log.debug("Multi-day booking | days={} | remainingHours={}", totalDays, remainingHours);

        BigDecimal dailyCost = BigDecimal.ZERO;
        BigDecimal hourlyCost = BigDecimal.ZERO;

        if (machine.getRatePerDay() != null) {
            dailyCost = machine.getRatePerDay().multiply(BigDecimal.valueOf(totalDays));
        }

        if (remainingHours > 0) {
            if (machine.getMinimumUsageHours() != null) {
                remainingHours = Math.max(remainingHours, machine.getMinimumUsageHours());
            }

            hourlyCost = machine.getRatePerHour().multiply(BigDecimal.valueOf(remainingHours));
        }

        log.info("Pricing decision: MIXED | daysCost={} | hoursCost={}", dailyCost, hourlyCost);

        dto.setTotalDailyRate(dailyCost.setScale(2, RoundingMode.HALF_UP));
        dto.setTotalHourlyRate(hourlyCost.setScale(2, RoundingMode.HALF_UP));

        log.info("PRICING END | machineId={} | hourly={} | daily={}", machine.getId(), dto.getTotalHourlyRate(), dto.getTotalDailyRate());

        return dto;
    }
}

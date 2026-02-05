package com.gearx7.app.web.rest;

import com.gearx7.app.repository.MachineRepository;
import com.gearx7.app.repository.UserRepository;
import com.gearx7.app.service.MachineService;
import com.gearx7.app.service.dto.MachineDTO;
import com.gearx7.app.service.mapper.UserMapper;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.gearx7.app.domain.Machine}.
 */
@RestController
@RequestMapping("/api/machines")
public class MachineResource {

    private final Logger log = LoggerFactory.getLogger(MachineResource.class);

    private static final String ENTITY_NAME = "machine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MachineService machineService;

    private final MachineRepository machineRepository;

    private final UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public MachineResource(MachineService machineService, MachineRepository machineRepository, UserRepository userRepository) {
        this.machineService = machineService;
        this.machineRepository = machineRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /machines} : Create a new machine.
     *
     * @param machineDTO the machineDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new machineDTO, or with status {@code 400 (Bad Request)} if the machine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<MachineDTO> createMachine(@Valid @RequestBody MachineDTO machineDTO) throws URISyntaxException {
        log.debug("REST request to save Machine : {}", machineDTO);
        if (machineDTO.getId() != null) {
            throw new BadRequestAlertException("A new machine cannot already have an ID", ENTITY_NAME, "idexists");
        }

        //        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
        //            log.debug("No user passed in, using current user: {}", SecurityUtils.getCurrentUserLogin().orElseThrow());
        //            String username = SecurityUtils.getCurrentUserLogin().orElseThrow();
        //            //machineDTO.setUser(userRepository.findOneByLogin(username).orElseThrow());
        //            User userEntity = userRepository.findOneByLogin(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        //            UserDTO userDTO = userMapper.userToUserDTO(userEntity); // Convert entity to DTO
        //            machineDTO.setUser(userDTO);
        //        }

        MachineDTO result = machineService.save(machineDTO);
        return ResponseEntity
            .created(new URI("/api/machines/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /machines/:id} : Updates an existing machine.
     *
     * @param id         the id of the machineDTO to save.
     * @param machineDTO the machineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated machineDTO,
     * or with status {@code 400 (Bad Request)} if the machineDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the machineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MachineDTO> updateMachine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MachineDTO machineDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Machine : {}, {}", id, machineDTO);
        if (machineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, machineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!machineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        MachineDTO result = machineService.update(machineDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, machineDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /machines/:id} : Partial updates given fields of an existing machine, field will ignore if it is null
     *
     * @param id         the id of the machineDTO to save.
     * @param machineDTO the machineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated machineDTO,
     * or with status {@code 400 (Bad Request)} if the machineDTO is not valid,
     * or with status {@code 404 (Not Found)} if the machineDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the machineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MachineDTO> partialUpdateMachine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MachineDTO machineDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Machine partially : {}, {}", id, machineDTO);
        if (machineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, machineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!machineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MachineDTO> result = machineService.partialUpdate(machineDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, machineDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /machines} : get all the machines.
     *
     * @param pageable  the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of machines in body.
     */
    @GetMapping("")
    public ResponseEntity<List<MachineDTO>> getAllMachines(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Machines");
        Page<MachineDTO> page;
        if (eagerload) {
            page = machineService.findAllWithEagerRelationships(pageable);
        } else {
            page = machineService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /machines/:id} : get the "id" machine.
     *
     * @param id the id of the machineDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the machineDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MachineDTO> getMachine(@PathVariable("id") Long id) {
        log.debug("REST request to get Machine : {}", id);
        return ResponseUtil.wrapOrNotFound(machineService.findOne(id));
    }

    /**
     * {@code DELETE  /machines/:id} : delete the "id" machine.
     *
     * @param id the id of the machineDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMachine(@PathVariable("id") Long id) {
        log.debug("REST request to delete Machine : {}", id);
        machineService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<MachineDTO>> searchMachines(
        @RequestParam Long typeId,
        @RequestParam Long categoryId,
        @RequestParam Long subcategoryId,
        @RequestParam String startDate,
        @RequestParam String endDate,
        @RequestParam Double lat,
        @RequestParam Double lon // here pass radius from API Request
    ) {
        log.info(
            "REST request to Search Machines received: typeId={}, categoryId={}, subcategoryId={}, startDate={}, endDate={}, lat={}, lon={}",
            typeId,
            categoryId,
            subcategoryId,
            startDate,
            endDate,
            lat,
            lon
        );

        if (typeId == null || categoryId == null || subcategoryId == null) {
            throw new BadRequestAlertException("Type, Category and Subcategory are required", ENTITY_NAME, "missingParams");
        }

        if (lat == null || lon == null) {
            throw new BadRequestAlertException("Location coordinates are required", ENTITY_NAME, "missingLocation");
        }

        // Parse start and end dates - handle multiple formats
        Instant startDateTime = parseDateTime(startDate);
        Instant endDateTime = parseDateTime(endDate);
        validateDates(startDateTime, endDateTime);

        List<MachineDTO> machines = machineService.searchMachines(typeId, categoryId, subcategoryId, startDateTime, endDateTime, lat, lon);

        if (machines == null || machines.isEmpty()) {
            log.info("Search completed. No machines found.");
            return ResponseEntity.noContent().build();
        }
        log.info("Search completed. {} machines found.", machines.size());
        return ResponseEntity.ok(machines);
    }

    /**
     * Parse date string in various formats to Instant.
     * Handles formats like:
     * - "2025-11-10T08:00" (without seconds and timezone)
     * - "2025-11-10T08:00:00" (without timezone)
     * - "2025-11-10T08:00:00Z" (with timezone)
     * - ISO-8601 full format
     */
    private Instant parseDateTime(String dateString) {
        log.debug("Parsing date: {}", dateString);

        try {
            // Try parsing as ISO-8601 Instant first (with timezone)
            return Instant.parse(dateString);
        } catch (DateTimeParseException e) {
            log.debug("Failed to parse ISO Instant, trying LocalDateTime format {}", dateString);

            try {
                // Try parsing as LocalDateTime without timezone (format: "2025-11-10T08:00" or "2025-11-10T08:00:00")
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
                // Convert to Instant assuming UTC timezone
                return localDateTime.toInstant(ZoneOffset.UTC);
            } catch (DateTimeParseException e2) {
                log.error("Invalid date format: {}", dateString);
                // If all parsing fails, throw a more descriptive error
                throw new BadRequestAlertException(
                    "Invalid date format: " + dateString + ". Expected format: YYYY-MM-DDTHH:mm or ISO-8601 format",
                    ENTITY_NAME,
                    "invalidDateFormat"
                );
            }
        }
    }

    private void validateDates(Instant start, Instant end) {
        Instant now = Instant.now();

        if (start.isBefore(now)) throw new BadRequestAlertException("Start date must be future", ENTITY_NAME, "startDateInPast");

        if (end.isBefore(now)) throw new BadRequestAlertException("End date must be future", ENTITY_NAME, "endDateInPast");

        if (!start.isBefore(end)) throw new BadRequestAlertException("Start date must be before end date", ENTITY_NAME, "invalidDateRange");
    }
}

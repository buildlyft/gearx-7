package com.gearx7.app.web.rest;

import com.gearx7.app.domain.User;
import com.gearx7.app.repository.MachineRepository;
import com.gearx7.app.repository.UserRepository;
import com.gearx7.app.security.AuthoritiesConstants;
import com.gearx7.app.security.SecurityUtils;
import com.gearx7.app.service.MachineService;
import com.gearx7.app.service.dto.MachineDTO;
import com.gearx7.app.service.dto.UserDTO;
import com.gearx7.app.service.mapper.UserMapper;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public ResponseEntity<MachineDTO> createMachine(@Valid @RequestBody MachineDTO machineDTO) throws URISyntaxException {
        log.debug("REST request to save Machine : {}", machineDTO);
        if (machineDTO.getId() != null) {
            throw new BadRequestAlertException("A new machine cannot already have an ID", ENTITY_NAME, "idexists");
        }

        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            log.debug("No user passed in, using current user: {}", SecurityUtils.getCurrentUserLogin().orElseThrow());
            String username = SecurityUtils.getCurrentUserLogin().orElseThrow();
            //machineDTO.setUser(userRepository.findOneByLogin(username).orElseThrow());
            User userEntity = userRepository.findOneByLogin(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            UserDTO userDTO = userMapper.userToUserDTO(userEntity); // Convert entity to DTO
            machineDTO.setUser(userDTO);
        }

        MachineDTO result = machineService.save(machineDTO);
        return ResponseEntity
            .created(new URI("/api/machines/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /machines/:id} : Updates an existing machine.
     *
     * @param id the id of the machineDTO to save.
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
     * @param id the id of the machineDTO to save.
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
     * @param pageable the pagination information.
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
        Optional<MachineDTO> machineDTO = machineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(machineDTO);
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
        @RequestParam Long categoryId,
        @RequestParam Long subcategoryId,
        @RequestParam String startDate,
        @RequestParam String endDate,
        @RequestParam Double lat,
        @RequestParam Double lon
    ) {
        // Mock user and partner DTOs
        UserDTO user = new UserDTO();
        user.setId(101L);
        user.setLogin("partnerUser1");
        user.setLogin("John");
        //        user.setLastName("Doe");

        // Mock machine DTOs
        MachineDTO machine1 = new MachineDTO();
        machine1.setId(1L);
        machine1.setBrand("Caterpillar");
        machine1.setType("Excavator");
        machine1.setTag("CAT-EX-300");
        machine1.setModel("320D");
        machine1.setVinNumber("VIN123456789");
        machine1.setChassisNumber("CH123456789");
        machine1.setDescription("Medium-duty excavator suitable for construction");
        machine1.setCapacityTon(20);
        machine1.setRatePerHour(new BigDecimal("1500.00"));
        machine1.setMinimumUsageHours(4);
        machine1.setLatitude(12.9611);
        machine1.setLongitude(77.6387);
        machine1.setTransportationCharge(new BigDecimal("500.00"));
        machine1.setDriverBatta(new BigDecimal("200.00"));
        machine1.setServiceabilityRangeKm(10);
        machine1.setStatus(com.gearx7.app.domain.enumeration.MachineStatus.AVAILABLE);
        machine1.setCreatedDate(Instant.parse("2025-10-01T10:00:00Z"));
        machine1.setUser(user);

        // Add more mock machines as needed in similar way

        List<MachineDTO> machines = Arrays.asList(machine1);

        // Optionally filter by location or other criteria using params (not shown for mock)
        return ResponseEntity.ok(machines);
    }
}

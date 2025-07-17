package com.gearx7.app.web.rest;

import static com.gearx7.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.gearx7.app.IntegrationTest;
import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.enumeration.MachineStatus;
import com.gearx7.app.repository.MachineRepository;
import com.gearx7.app.service.MachineService;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link MachineResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MachineResourceIT {

    private static final String DEFAULT_BRAND = "AAAAAAAAAA";
    private static final String UPDATED_BRAND = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_TAG = "AAAAAAAAAA";
    private static final String UPDATED_TAG = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_MODEL = "BBBBBBBBBB";

    private static final String DEFAULT_VIN_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_VIN_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_CHASSIS_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CHASSIS_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_CAPACITY_TON = 1;
    private static final Integer UPDATED_CAPACITY_TON = 2;

    private static final BigDecimal DEFAULT_RATE_PER_HOUR = new BigDecimal(1);
    private static final BigDecimal UPDATED_RATE_PER_HOUR = new BigDecimal(2);

    private static final Integer DEFAULT_MINIMUM_USAGE_HOURS = 1;
    private static final Integer UPDATED_MINIMUM_USAGE_HOURS = 2;

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    private static final BigDecimal DEFAULT_TRANSPORTATION_CHARGE = new BigDecimal(1);
    private static final BigDecimal UPDATED_TRANSPORTATION_CHARGE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_DRIVER_BATTA = new BigDecimal(1);
    private static final BigDecimal UPDATED_DRIVER_BATTA = new BigDecimal(2);

    private static final Integer DEFAULT_SERVICEABILITY_RANGE_KM = 1;
    private static final Integer UPDATED_SERVICEABILITY_RANGE_KM = 2;

    private static final MachineStatus DEFAULT_STATUS = MachineStatus.AVAILABLE;
    private static final MachineStatus UPDATED_STATUS = MachineStatus.NOT_AVAILABLE;

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/machines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MachineRepository machineRepository;

    @Mock
    private MachineRepository machineRepositoryMock;

    @Mock
    private MachineService machineServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMachineMockMvc;

    private Machine machine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Machine createEntity(EntityManager em) {
        Machine machine = new Machine()
            .brand(DEFAULT_BRAND)
            .type(DEFAULT_TYPE)
            .tag(DEFAULT_TAG)
            .model(DEFAULT_MODEL)
            .vinNumber(DEFAULT_VIN_NUMBER)
            .chassisNumber(DEFAULT_CHASSIS_NUMBER)
            .description(DEFAULT_DESCRIPTION)
            .capacityTon(DEFAULT_CAPACITY_TON)
            .ratePerHour(DEFAULT_RATE_PER_HOUR)
            .minimumUsageHours(DEFAULT_MINIMUM_USAGE_HOURS)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .transportationCharge(DEFAULT_TRANSPORTATION_CHARGE)
            .driverBatta(DEFAULT_DRIVER_BATTA)
            .serviceabilityRangeKm(DEFAULT_SERVICEABILITY_RANGE_KM)
            .status(DEFAULT_STATUS)
            .createdDate(DEFAULT_CREATED_DATE);
        return machine;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Machine createUpdatedEntity(EntityManager em) {
        Machine machine = new Machine()
            .brand(UPDATED_BRAND)
            .type(UPDATED_TYPE)
            .tag(UPDATED_TAG)
            .model(UPDATED_MODEL)
            .vinNumber(UPDATED_VIN_NUMBER)
            .chassisNumber(UPDATED_CHASSIS_NUMBER)
            .description(UPDATED_DESCRIPTION)
            .capacityTon(UPDATED_CAPACITY_TON)
            .ratePerHour(UPDATED_RATE_PER_HOUR)
            .minimumUsageHours(UPDATED_MINIMUM_USAGE_HOURS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .transportationCharge(UPDATED_TRANSPORTATION_CHARGE)
            .driverBatta(UPDATED_DRIVER_BATTA)
            .serviceabilityRangeKm(UPDATED_SERVICEABILITY_RANGE_KM)
            .status(UPDATED_STATUS)
            .createdDate(UPDATED_CREATED_DATE);
        return machine;
    }

    @BeforeEach
    public void initTest() {
        machine = createEntity(em);
    }

    @Test
    @Transactional
    void createMachine() throws Exception {
        int databaseSizeBeforeCreate = machineRepository.findAll().size();
        // Create the Machine
        restMachineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(machine)))
            .andExpect(status().isCreated());

        // Validate the Machine in the database
        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeCreate + 1);
        Machine testMachine = machineList.get(machineList.size() - 1);
        assertThat(testMachine.getBrand()).isEqualTo(DEFAULT_BRAND);
        assertThat(testMachine.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testMachine.getTag()).isEqualTo(DEFAULT_TAG);
        assertThat(testMachine.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testMachine.getVinNumber()).isEqualTo(DEFAULT_VIN_NUMBER);
        assertThat(testMachine.getChassisNumber()).isEqualTo(DEFAULT_CHASSIS_NUMBER);
        assertThat(testMachine.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMachine.getCapacityTon()).isEqualTo(DEFAULT_CAPACITY_TON);
        assertThat(testMachine.getRatePerHour()).isEqualByComparingTo(DEFAULT_RATE_PER_HOUR);
        assertThat(testMachine.getMinimumUsageHours()).isEqualTo(DEFAULT_MINIMUM_USAGE_HOURS);
        assertThat(testMachine.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testMachine.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testMachine.getTransportationCharge()).isEqualByComparingTo(DEFAULT_TRANSPORTATION_CHARGE);
        assertThat(testMachine.getDriverBatta()).isEqualByComparingTo(DEFAULT_DRIVER_BATTA);
        assertThat(testMachine.getServiceabilityRangeKm()).isEqualTo(DEFAULT_SERVICEABILITY_RANGE_KM);
        assertThat(testMachine.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testMachine.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
    }

    @Test
    @Transactional
    void createMachineWithExistingId() throws Exception {
        // Create the Machine with an existing ID
        machine.setId(1L);

        int databaseSizeBeforeCreate = machineRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMachineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(machine)))
            .andExpect(status().isBadRequest());

        // Validate the Machine in the database
        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBrandIsRequired() throws Exception {
        int databaseSizeBeforeTest = machineRepository.findAll().size();
        // set the field null
        machine.setBrand(null);

        // Create the Machine, which fails.

        restMachineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(machine)))
            .andExpect(status().isBadRequest());

        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = machineRepository.findAll().size();
        // set the field null
        machine.setType(null);

        // Create the Machine, which fails.

        restMachineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(machine)))
            .andExpect(status().isBadRequest());

        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRatePerHourIsRequired() throws Exception {
        int databaseSizeBeforeTest = machineRepository.findAll().size();
        // set the field null
        machine.setRatePerHour(null);

        // Create the Machine, which fails.

        restMachineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(machine)))
            .andExpect(status().isBadRequest());

        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLatitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = machineRepository.findAll().size();
        // set the field null
        machine.setLatitude(null);

        // Create the Machine, which fails.

        restMachineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(machine)))
            .andExpect(status().isBadRequest());

        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLongitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = machineRepository.findAll().size();
        // set the field null
        machine.setLongitude(null);

        // Create the Machine, which fails.

        restMachineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(machine)))
            .andExpect(status().isBadRequest());

        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = machineRepository.findAll().size();
        // set the field null
        machine.setStatus(null);

        // Create the Machine, which fails.

        restMachineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(machine)))
            .andExpect(status().isBadRequest());

        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = machineRepository.findAll().size();
        // set the field null
        machine.setCreatedDate(null);

        // Create the Machine, which fails.

        restMachineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(machine)))
            .andExpect(status().isBadRequest());

        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMachines() throws Exception {
        // Initialize the database
        machineRepository.saveAndFlush(machine);

        // Get all the machineList
        restMachineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(machine.getId().intValue())))
            .andExpect(jsonPath("$.[*].brand").value(hasItem(DEFAULT_BRAND)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].tag").value(hasItem(DEFAULT_TAG)))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)))
            .andExpect(jsonPath("$.[*].vinNumber").value(hasItem(DEFAULT_VIN_NUMBER)))
            .andExpect(jsonPath("$.[*].chassisNumber").value(hasItem(DEFAULT_CHASSIS_NUMBER)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].capacityTon").value(hasItem(DEFAULT_CAPACITY_TON)))
            .andExpect(jsonPath("$.[*].ratePerHour").value(hasItem(sameNumber(DEFAULT_RATE_PER_HOUR))))
            .andExpect(jsonPath("$.[*].minimumUsageHours").value(hasItem(DEFAULT_MINIMUM_USAGE_HOURS)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].transportationCharge").value(hasItem(sameNumber(DEFAULT_TRANSPORTATION_CHARGE))))
            .andExpect(jsonPath("$.[*].driverBatta").value(hasItem(sameNumber(DEFAULT_DRIVER_BATTA))))
            .andExpect(jsonPath("$.[*].serviceabilityRangeKm").value(hasItem(DEFAULT_SERVICEABILITY_RANGE_KM)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMachinesWithEagerRelationshipsIsEnabled() throws Exception {
        when(machineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMachineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(machineServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMachinesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(machineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMachineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(machineRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMachine() throws Exception {
        // Initialize the database
        machineRepository.saveAndFlush(machine);

        // Get the machine
        restMachineMockMvc
            .perform(get(ENTITY_API_URL_ID, machine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(machine.getId().intValue()))
            .andExpect(jsonPath("$.brand").value(DEFAULT_BRAND))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.tag").value(DEFAULT_TAG))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL))
            .andExpect(jsonPath("$.vinNumber").value(DEFAULT_VIN_NUMBER))
            .andExpect(jsonPath("$.chassisNumber").value(DEFAULT_CHASSIS_NUMBER))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.capacityTon").value(DEFAULT_CAPACITY_TON))
            .andExpect(jsonPath("$.ratePerHour").value(sameNumber(DEFAULT_RATE_PER_HOUR)))
            .andExpect(jsonPath("$.minimumUsageHours").value(DEFAULT_MINIMUM_USAGE_HOURS))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.transportationCharge").value(sameNumber(DEFAULT_TRANSPORTATION_CHARGE)))
            .andExpect(jsonPath("$.driverBatta").value(sameNumber(DEFAULT_DRIVER_BATTA)))
            .andExpect(jsonPath("$.serviceabilityRangeKm").value(DEFAULT_SERVICEABILITY_RANGE_KM))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingMachine() throws Exception {
        // Get the machine
        restMachineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMachine() throws Exception {
        // Initialize the database
        machineRepository.saveAndFlush(machine);

        int databaseSizeBeforeUpdate = machineRepository.findAll().size();

        // Update the machine
        Machine updatedMachine = machineRepository.findById(machine.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMachine are not directly saved in db
        em.detach(updatedMachine);
        updatedMachine
            .brand(UPDATED_BRAND)
            .type(UPDATED_TYPE)
            .tag(UPDATED_TAG)
            .model(UPDATED_MODEL)
            .vinNumber(UPDATED_VIN_NUMBER)
            .chassisNumber(UPDATED_CHASSIS_NUMBER)
            .description(UPDATED_DESCRIPTION)
            .capacityTon(UPDATED_CAPACITY_TON)
            .ratePerHour(UPDATED_RATE_PER_HOUR)
            .minimumUsageHours(UPDATED_MINIMUM_USAGE_HOURS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .transportationCharge(UPDATED_TRANSPORTATION_CHARGE)
            .driverBatta(UPDATED_DRIVER_BATTA)
            .serviceabilityRangeKm(UPDATED_SERVICEABILITY_RANGE_KM)
            .status(UPDATED_STATUS)
            .createdDate(UPDATED_CREATED_DATE);

        restMachineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMachine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedMachine))
            )
            .andExpect(status().isOk());

        // Validate the Machine in the database
        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeUpdate);
        Machine testMachine = machineList.get(machineList.size() - 1);
        assertThat(testMachine.getBrand()).isEqualTo(UPDATED_BRAND);
        assertThat(testMachine.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testMachine.getTag()).isEqualTo(UPDATED_TAG);
        assertThat(testMachine.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testMachine.getVinNumber()).isEqualTo(UPDATED_VIN_NUMBER);
        assertThat(testMachine.getChassisNumber()).isEqualTo(UPDATED_CHASSIS_NUMBER);
        assertThat(testMachine.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMachine.getCapacityTon()).isEqualTo(UPDATED_CAPACITY_TON);
        assertThat(testMachine.getRatePerHour()).isEqualByComparingTo(UPDATED_RATE_PER_HOUR);
        assertThat(testMachine.getMinimumUsageHours()).isEqualTo(UPDATED_MINIMUM_USAGE_HOURS);
        assertThat(testMachine.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testMachine.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testMachine.getTransportationCharge()).isEqualByComparingTo(UPDATED_TRANSPORTATION_CHARGE);
        assertThat(testMachine.getDriverBatta()).isEqualByComparingTo(UPDATED_DRIVER_BATTA);
        assertThat(testMachine.getServiceabilityRangeKm()).isEqualTo(UPDATED_SERVICEABILITY_RANGE_KM);
        assertThat(testMachine.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testMachine.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingMachine() throws Exception {
        int databaseSizeBeforeUpdate = machineRepository.findAll().size();
        machine.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMachineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, machine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(machine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Machine in the database
        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMachine() throws Exception {
        int databaseSizeBeforeUpdate = machineRepository.findAll().size();
        machine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(machine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Machine in the database
        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMachine() throws Exception {
        int databaseSizeBeforeUpdate = machineRepository.findAll().size();
        machine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(machine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Machine in the database
        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMachineWithPatch() throws Exception {
        // Initialize the database
        machineRepository.saveAndFlush(machine);

        int databaseSizeBeforeUpdate = machineRepository.findAll().size();

        // Update the machine using partial update
        Machine partialUpdatedMachine = new Machine();
        partialUpdatedMachine.setId(machine.getId());

        partialUpdatedMachine
            .brand(UPDATED_BRAND)
            .tag(UPDATED_TAG)
            .model(UPDATED_MODEL)
            .vinNumber(UPDATED_VIN_NUMBER)
            .chassisNumber(UPDATED_CHASSIS_NUMBER)
            .description(UPDATED_DESCRIPTION)
            .ratePerHour(UPDATED_RATE_PER_HOUR)
            .minimumUsageHours(UPDATED_MINIMUM_USAGE_HOURS)
            .longitude(UPDATED_LONGITUDE)
            .transportationCharge(UPDATED_TRANSPORTATION_CHARGE)
            .serviceabilityRangeKm(UPDATED_SERVICEABILITY_RANGE_KM)
            .status(UPDATED_STATUS)
            .createdDate(UPDATED_CREATED_DATE);

        restMachineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMachine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMachine))
            )
            .andExpect(status().isOk());

        // Validate the Machine in the database
        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeUpdate);
        Machine testMachine = machineList.get(machineList.size() - 1);
        assertThat(testMachine.getBrand()).isEqualTo(UPDATED_BRAND);
        assertThat(testMachine.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testMachine.getTag()).isEqualTo(UPDATED_TAG);
        assertThat(testMachine.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testMachine.getVinNumber()).isEqualTo(UPDATED_VIN_NUMBER);
        assertThat(testMachine.getChassisNumber()).isEqualTo(UPDATED_CHASSIS_NUMBER);
        assertThat(testMachine.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMachine.getCapacityTon()).isEqualTo(DEFAULT_CAPACITY_TON);
        assertThat(testMachine.getRatePerHour()).isEqualByComparingTo(UPDATED_RATE_PER_HOUR);
        assertThat(testMachine.getMinimumUsageHours()).isEqualTo(UPDATED_MINIMUM_USAGE_HOURS);
        assertThat(testMachine.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testMachine.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testMachine.getTransportationCharge()).isEqualByComparingTo(UPDATED_TRANSPORTATION_CHARGE);
        assertThat(testMachine.getDriverBatta()).isEqualByComparingTo(DEFAULT_DRIVER_BATTA);
        assertThat(testMachine.getServiceabilityRangeKm()).isEqualTo(UPDATED_SERVICEABILITY_RANGE_KM);
        assertThat(testMachine.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testMachine.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateMachineWithPatch() throws Exception {
        // Initialize the database
        machineRepository.saveAndFlush(machine);

        int databaseSizeBeforeUpdate = machineRepository.findAll().size();

        // Update the machine using partial update
        Machine partialUpdatedMachine = new Machine();
        partialUpdatedMachine.setId(machine.getId());

        partialUpdatedMachine
            .brand(UPDATED_BRAND)
            .type(UPDATED_TYPE)
            .tag(UPDATED_TAG)
            .model(UPDATED_MODEL)
            .vinNumber(UPDATED_VIN_NUMBER)
            .chassisNumber(UPDATED_CHASSIS_NUMBER)
            .description(UPDATED_DESCRIPTION)
            .capacityTon(UPDATED_CAPACITY_TON)
            .ratePerHour(UPDATED_RATE_PER_HOUR)
            .minimumUsageHours(UPDATED_MINIMUM_USAGE_HOURS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .transportationCharge(UPDATED_TRANSPORTATION_CHARGE)
            .driverBatta(UPDATED_DRIVER_BATTA)
            .serviceabilityRangeKm(UPDATED_SERVICEABILITY_RANGE_KM)
            .status(UPDATED_STATUS)
            .createdDate(UPDATED_CREATED_DATE);

        restMachineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMachine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMachine))
            )
            .andExpect(status().isOk());

        // Validate the Machine in the database
        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeUpdate);
        Machine testMachine = machineList.get(machineList.size() - 1);
        assertThat(testMachine.getBrand()).isEqualTo(UPDATED_BRAND);
        assertThat(testMachine.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testMachine.getTag()).isEqualTo(UPDATED_TAG);
        assertThat(testMachine.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testMachine.getVinNumber()).isEqualTo(UPDATED_VIN_NUMBER);
        assertThat(testMachine.getChassisNumber()).isEqualTo(UPDATED_CHASSIS_NUMBER);
        assertThat(testMachine.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMachine.getCapacityTon()).isEqualTo(UPDATED_CAPACITY_TON);
        assertThat(testMachine.getRatePerHour()).isEqualByComparingTo(UPDATED_RATE_PER_HOUR);
        assertThat(testMachine.getMinimumUsageHours()).isEqualTo(UPDATED_MINIMUM_USAGE_HOURS);
        assertThat(testMachine.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testMachine.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testMachine.getTransportationCharge()).isEqualByComparingTo(UPDATED_TRANSPORTATION_CHARGE);
        assertThat(testMachine.getDriverBatta()).isEqualByComparingTo(UPDATED_DRIVER_BATTA);
        assertThat(testMachine.getServiceabilityRangeKm()).isEqualTo(UPDATED_SERVICEABILITY_RANGE_KM);
        assertThat(testMachine.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testMachine.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingMachine() throws Exception {
        int databaseSizeBeforeUpdate = machineRepository.findAll().size();
        machine.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMachineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, machine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(machine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Machine in the database
        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMachine() throws Exception {
        int databaseSizeBeforeUpdate = machineRepository.findAll().size();
        machine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(machine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Machine in the database
        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMachine() throws Exception {
        int databaseSizeBeforeUpdate = machineRepository.findAll().size();
        machine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMachineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(machine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Machine in the database
        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMachine() throws Exception {
        // Initialize the database
        machineRepository.saveAndFlush(machine);

        int databaseSizeBeforeDelete = machineRepository.findAll().size();

        // Delete the machine
        restMachineMockMvc
            .perform(delete(ENTITY_API_URL_ID, machine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Machine> machineList = machineRepository.findAll();
        assertThat(machineList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

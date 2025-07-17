package com.gearx7.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.gearx7.app.IntegrationTest;
import com.gearx7.app.domain.Booking;
import com.gearx7.app.domain.enumeration.BookingStatus;
import com.gearx7.app.repository.BookingRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BookingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BookingResourceIT {

    private static final Instant DEFAULT_START_DATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BookingStatus DEFAULT_STATUS = BookingStatus.PENDING;
    private static final BookingStatus UPDATED_STATUS = BookingStatus.ACCEPTED;

    private static final String DEFAULT_ADDITIONAL_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_ADDITIONAL_DETAILS = "BBBBBBBBBB";

    private static final String DEFAULT_WORKSITE_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_WORKSITE_IMAGE_URL = "BBBBBBBBBB";

    private static final Double DEFAULT_CUSTOMER_LAT = 1D;
    private static final Double UPDATED_CUSTOMER_LAT = 2D;

    private static final Double DEFAULT_CUSTOMER_LONG = 1D;
    private static final Double UPDATED_CUSTOMER_LONG = 2D;

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/bookings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookingMockMvc;

    private Booking booking;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Booking createEntity(EntityManager em) {
        Booking booking = new Booking()
            .startDateTime(DEFAULT_START_DATE_TIME)
            .endDateTime(DEFAULT_END_DATE_TIME)
            .status(DEFAULT_STATUS)
            .additionalDetails(DEFAULT_ADDITIONAL_DETAILS)
            .worksiteImageUrl(DEFAULT_WORKSITE_IMAGE_URL)
            .customerLat(DEFAULT_CUSTOMER_LAT)
            .customerLong(DEFAULT_CUSTOMER_LONG)
            .createdDate(DEFAULT_CREATED_DATE);
        return booking;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Booking createUpdatedEntity(EntityManager em) {
        Booking booking = new Booking()
            .startDateTime(UPDATED_START_DATE_TIME)
            .endDateTime(UPDATED_END_DATE_TIME)
            .status(UPDATED_STATUS)
            .additionalDetails(UPDATED_ADDITIONAL_DETAILS)
            .worksiteImageUrl(UPDATED_WORKSITE_IMAGE_URL)
            .customerLat(UPDATED_CUSTOMER_LAT)
            .customerLong(UPDATED_CUSTOMER_LONG)
            .createdDate(UPDATED_CREATED_DATE);
        return booking;
    }

    @BeforeEach
    public void initTest() {
        booking = createEntity(em);
    }

    @Test
    @Transactional
    void createBooking() throws Exception {
        int databaseSizeBeforeCreate = bookingRepository.findAll().size();
        // Create the Booking
        restBookingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(booking)))
            .andExpect(status().isCreated());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeCreate + 1);
        Booking testBooking = bookingList.get(bookingList.size() - 1);
        assertThat(testBooking.getStartDateTime()).isEqualTo(DEFAULT_START_DATE_TIME);
        assertThat(testBooking.getEndDateTime()).isEqualTo(DEFAULT_END_DATE_TIME);
        assertThat(testBooking.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testBooking.getAdditionalDetails()).isEqualTo(DEFAULT_ADDITIONAL_DETAILS);
        assertThat(testBooking.getWorksiteImageUrl()).isEqualTo(DEFAULT_WORKSITE_IMAGE_URL);
        assertThat(testBooking.getCustomerLat()).isEqualTo(DEFAULT_CUSTOMER_LAT);
        assertThat(testBooking.getCustomerLong()).isEqualTo(DEFAULT_CUSTOMER_LONG);
        assertThat(testBooking.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
    }

    @Test
    @Transactional
    void createBookingWithExistingId() throws Exception {
        // Create the Booking with an existing ID
        booking.setId(1L);

        int databaseSizeBeforeCreate = bookingRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(booking)))
            .andExpect(status().isBadRequest());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartDateTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookingRepository.findAll().size();
        // set the field null
        booking.setStartDateTime(null);

        // Create the Booking, which fails.

        restBookingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(booking)))
            .andExpect(status().isBadRequest());

        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookingRepository.findAll().size();
        // set the field null
        booking.setEndDateTime(null);

        // Create the Booking, which fails.

        restBookingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(booking)))
            .andExpect(status().isBadRequest());

        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookingRepository.findAll().size();
        // set the field null
        booking.setStatus(null);

        // Create the Booking, which fails.

        restBookingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(booking)))
            .andExpect(status().isBadRequest());

        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBookings() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get all the bookingList
        restBookingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(booking.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDateTime").value(hasItem(DEFAULT_START_DATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].endDateTime").value(hasItem(DEFAULT_END_DATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].additionalDetails").value(hasItem(DEFAULT_ADDITIONAL_DETAILS)))
            .andExpect(jsonPath("$.[*].worksiteImageUrl").value(hasItem(DEFAULT_WORKSITE_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].customerLat").value(hasItem(DEFAULT_CUSTOMER_LAT.doubleValue())))
            .andExpect(jsonPath("$.[*].customerLong").value(hasItem(DEFAULT_CUSTOMER_LONG.doubleValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())));
    }

    @Test
    @Transactional
    void getBooking() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        // Get the booking
        restBookingMockMvc
            .perform(get(ENTITY_API_URL_ID, booking.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(booking.getId().intValue()))
            .andExpect(jsonPath("$.startDateTime").value(DEFAULT_START_DATE_TIME.toString()))
            .andExpect(jsonPath("$.endDateTime").value(DEFAULT_END_DATE_TIME.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.additionalDetails").value(DEFAULT_ADDITIONAL_DETAILS))
            .andExpect(jsonPath("$.worksiteImageUrl").value(DEFAULT_WORKSITE_IMAGE_URL))
            .andExpect(jsonPath("$.customerLat").value(DEFAULT_CUSTOMER_LAT.doubleValue()))
            .andExpect(jsonPath("$.customerLong").value(DEFAULT_CUSTOMER_LONG.doubleValue()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBooking() throws Exception {
        // Get the booking
        restBookingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBooking() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();

        // Update the booking
        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBooking are not directly saved in db
        em.detach(updatedBooking);
        updatedBooking
            .startDateTime(UPDATED_START_DATE_TIME)
            .endDateTime(UPDATED_END_DATE_TIME)
            .status(UPDATED_STATUS)
            .additionalDetails(UPDATED_ADDITIONAL_DETAILS)
            .worksiteImageUrl(UPDATED_WORKSITE_IMAGE_URL)
            .customerLat(UPDATED_CUSTOMER_LAT)
            .customerLong(UPDATED_CUSTOMER_LONG)
            .createdDate(UPDATED_CREATED_DATE);

        restBookingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBooking.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBooking))
            )
            .andExpect(status().isOk());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
        Booking testBooking = bookingList.get(bookingList.size() - 1);
        assertThat(testBooking.getStartDateTime()).isEqualTo(UPDATED_START_DATE_TIME);
        assertThat(testBooking.getEndDateTime()).isEqualTo(UPDATED_END_DATE_TIME);
        assertThat(testBooking.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBooking.getAdditionalDetails()).isEqualTo(UPDATED_ADDITIONAL_DETAILS);
        assertThat(testBooking.getWorksiteImageUrl()).isEqualTo(UPDATED_WORKSITE_IMAGE_URL);
        assertThat(testBooking.getCustomerLat()).isEqualTo(UPDATED_CUSTOMER_LAT);
        assertThat(testBooking.getCustomerLong()).isEqualTo(UPDATED_CUSTOMER_LONG);
        assertThat(testBooking.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingBooking() throws Exception {
        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();
        booking.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, booking.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(booking))
            )
            .andExpect(status().isBadRequest());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBooking() throws Exception {
        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();
        booking.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(booking))
            )
            .andExpect(status().isBadRequest());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBooking() throws Exception {
        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();
        booking.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(booking)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookingWithPatch() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();

        // Update the booking using partial update
        Booking partialUpdatedBooking = new Booking();
        partialUpdatedBooking.setId(booking.getId());

        partialUpdatedBooking
            .startDateTime(UPDATED_START_DATE_TIME)
            .endDateTime(UPDATED_END_DATE_TIME)
            .additionalDetails(UPDATED_ADDITIONAL_DETAILS)
            .customerLat(UPDATED_CUSTOMER_LAT);

        restBookingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBooking.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBooking))
            )
            .andExpect(status().isOk());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
        Booking testBooking = bookingList.get(bookingList.size() - 1);
        assertThat(testBooking.getStartDateTime()).isEqualTo(UPDATED_START_DATE_TIME);
        assertThat(testBooking.getEndDateTime()).isEqualTo(UPDATED_END_DATE_TIME);
        assertThat(testBooking.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testBooking.getAdditionalDetails()).isEqualTo(UPDATED_ADDITIONAL_DETAILS);
        assertThat(testBooking.getWorksiteImageUrl()).isEqualTo(DEFAULT_WORKSITE_IMAGE_URL);
        assertThat(testBooking.getCustomerLat()).isEqualTo(UPDATED_CUSTOMER_LAT);
        assertThat(testBooking.getCustomerLong()).isEqualTo(DEFAULT_CUSTOMER_LONG);
        assertThat(testBooking.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateBookingWithPatch() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();

        // Update the booking using partial update
        Booking partialUpdatedBooking = new Booking();
        partialUpdatedBooking.setId(booking.getId());

        partialUpdatedBooking
            .startDateTime(UPDATED_START_DATE_TIME)
            .endDateTime(UPDATED_END_DATE_TIME)
            .status(UPDATED_STATUS)
            .additionalDetails(UPDATED_ADDITIONAL_DETAILS)
            .worksiteImageUrl(UPDATED_WORKSITE_IMAGE_URL)
            .customerLat(UPDATED_CUSTOMER_LAT)
            .customerLong(UPDATED_CUSTOMER_LONG)
            .createdDate(UPDATED_CREATED_DATE);

        restBookingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBooking.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBooking))
            )
            .andExpect(status().isOk());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
        Booking testBooking = bookingList.get(bookingList.size() - 1);
        assertThat(testBooking.getStartDateTime()).isEqualTo(UPDATED_START_DATE_TIME);
        assertThat(testBooking.getEndDateTime()).isEqualTo(UPDATED_END_DATE_TIME);
        assertThat(testBooking.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBooking.getAdditionalDetails()).isEqualTo(UPDATED_ADDITIONAL_DETAILS);
        assertThat(testBooking.getWorksiteImageUrl()).isEqualTo(UPDATED_WORKSITE_IMAGE_URL);
        assertThat(testBooking.getCustomerLat()).isEqualTo(UPDATED_CUSTOMER_LAT);
        assertThat(testBooking.getCustomerLong()).isEqualTo(UPDATED_CUSTOMER_LONG);
        assertThat(testBooking.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingBooking() throws Exception {
        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();
        booking.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, booking.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(booking))
            )
            .andExpect(status().isBadRequest());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBooking() throws Exception {
        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();
        booking.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(booking))
            )
            .andExpect(status().isBadRequest());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBooking() throws Exception {
        int databaseSizeBeforeUpdate = bookingRepository.findAll().size();
        booking.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(booking)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Booking in the database
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBooking() throws Exception {
        // Initialize the database
        bookingRepository.saveAndFlush(booking);

        int databaseSizeBeforeDelete = bookingRepository.findAll().size();

        // Delete the booking
        restBookingMockMvc
            .perform(delete(ENTITY_API_URL_ID, booking.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

package com.gearx7.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.gearx7.app.IntegrationTest;
import com.gearx7.app.domain.Partner;
import com.gearx7.app.repository.PartnerRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link PartnerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PartnerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMPANY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_PREFERRED_CONTACT_TIME = "AAAAAAAAAA";
    private static final String UPDATED_PREFERRED_CONTACT_TIME = "BBBBBBBBBB";

    private static final String DEFAULT_GST_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_GST_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_PAN_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PAN_NUMBER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/partners";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartnerMockMvc;

    private Partner partner;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Partner createEntity(EntityManager em) {
        Partner partner = new Partner()
            .name(DEFAULT_NAME)
            .companyName(DEFAULT_COMPANY_NAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .address(DEFAULT_ADDRESS)
            .preferredContactTime(DEFAULT_PREFERRED_CONTACT_TIME)
            .gstNumber(DEFAULT_GST_NUMBER)
            .panNumber(DEFAULT_PAN_NUMBER);
        return partner;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Partner createUpdatedEntity(EntityManager em) {
        Partner partner = new Partner()
            .name(UPDATED_NAME)
            .companyName(UPDATED_COMPANY_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .preferredContactTime(UPDATED_PREFERRED_CONTACT_TIME)
            .gstNumber(UPDATED_GST_NUMBER)
            .panNumber(UPDATED_PAN_NUMBER);
        return partner;
    }

    @BeforeEach
    public void initTest() {
        partner = createEntity(em);
    }

    @Test
    @Transactional
    void createPartner() throws Exception {
        int databaseSizeBeforeCreate = partnerRepository.findAll().size();
        // Create the Partner
        restPartnerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(partner)))
            .andExpect(status().isCreated());

        // Validate the Partner in the database
        List<Partner> partnerList = partnerRepository.findAll();
        assertThat(partnerList).hasSize(databaseSizeBeforeCreate + 1);
        Partner testPartner = partnerList.get(partnerList.size() - 1);
        assertThat(testPartner.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPartner.getCompanyName()).isEqualTo(DEFAULT_COMPANY_NAME);
        assertThat(testPartner.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testPartner.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testPartner.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testPartner.getPreferredContactTime()).isEqualTo(DEFAULT_PREFERRED_CONTACT_TIME);
        assertThat(testPartner.getGstNumber()).isEqualTo(DEFAULT_GST_NUMBER);
        assertThat(testPartner.getPanNumber()).isEqualTo(DEFAULT_PAN_NUMBER);
    }

    @Test
    @Transactional
    void createPartnerWithExistingId() throws Exception {
        // Create the Partner with an existing ID
        partner.setId(1L);

        int databaseSizeBeforeCreate = partnerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartnerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(partner)))
            .andExpect(status().isBadRequest());

        // Validate the Partner in the database
        List<Partner> partnerList = partnerRepository.findAll();
        assertThat(partnerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = partnerRepository.findAll().size();
        // set the field null
        partner.setName(null);

        // Create the Partner, which fails.

        restPartnerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(partner)))
            .andExpect(status().isBadRequest());

        List<Partner> partnerList = partnerRepository.findAll();
        assertThat(partnerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = partnerRepository.findAll().size();
        // set the field null
        partner.setPhone(null);

        // Create the Partner, which fails.

        restPartnerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(partner)))
            .andExpect(status().isBadRequest());

        List<Partner> partnerList = partnerRepository.findAll();
        assertThat(partnerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPartners() throws Exception {
        // Initialize the database
        partnerRepository.saveAndFlush(partner);

        // Get all the partnerList
        restPartnerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partner.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].companyName").value(hasItem(DEFAULT_COMPANY_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].preferredContactTime").value(hasItem(DEFAULT_PREFERRED_CONTACT_TIME)))
            .andExpect(jsonPath("$.[*].gstNumber").value(hasItem(DEFAULT_GST_NUMBER)))
            .andExpect(jsonPath("$.[*].panNumber").value(hasItem(DEFAULT_PAN_NUMBER)));
    }

    @Test
    @Transactional
    void getPartner() throws Exception {
        // Initialize the database
        partnerRepository.saveAndFlush(partner);

        // Get the partner
        restPartnerMockMvc
            .perform(get(ENTITY_API_URL_ID, partner.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(partner.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.companyName").value(DEFAULT_COMPANY_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.preferredContactTime").value(DEFAULT_PREFERRED_CONTACT_TIME))
            .andExpect(jsonPath("$.gstNumber").value(DEFAULT_GST_NUMBER))
            .andExpect(jsonPath("$.panNumber").value(DEFAULT_PAN_NUMBER));
    }

    @Test
    @Transactional
    void getNonExistingPartner() throws Exception {
        // Get the partner
        restPartnerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPartner() throws Exception {
        // Initialize the database
        partnerRepository.saveAndFlush(partner);

        int databaseSizeBeforeUpdate = partnerRepository.findAll().size();

        // Update the partner
        Partner updatedPartner = partnerRepository.findById(partner.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPartner are not directly saved in db
        em.detach(updatedPartner);
        updatedPartner
            .name(UPDATED_NAME)
            .companyName(UPDATED_COMPANY_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .preferredContactTime(UPDATED_PREFERRED_CONTACT_TIME)
            .gstNumber(UPDATED_GST_NUMBER)
            .panNumber(UPDATED_PAN_NUMBER);

        restPartnerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPartner.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPartner))
            )
            .andExpect(status().isOk());

        // Validate the Partner in the database
        List<Partner> partnerList = partnerRepository.findAll();
        assertThat(partnerList).hasSize(databaseSizeBeforeUpdate);
        Partner testPartner = partnerList.get(partnerList.size() - 1);
        assertThat(testPartner.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPartner.getCompanyName()).isEqualTo(UPDATED_COMPANY_NAME);
        assertThat(testPartner.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testPartner.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testPartner.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testPartner.getPreferredContactTime()).isEqualTo(UPDATED_PREFERRED_CONTACT_TIME);
        assertThat(testPartner.getGstNumber()).isEqualTo(UPDATED_GST_NUMBER);
        assertThat(testPartner.getPanNumber()).isEqualTo(UPDATED_PAN_NUMBER);
    }

    @Test
    @Transactional
    void putNonExistingPartner() throws Exception {
        int databaseSizeBeforeUpdate = partnerRepository.findAll().size();
        partner.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartnerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partner.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partner))
            )
            .andExpect(status().isBadRequest());

        // Validate the Partner in the database
        List<Partner> partnerList = partnerRepository.findAll();
        assertThat(partnerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPartner() throws Exception {
        int databaseSizeBeforeUpdate = partnerRepository.findAll().size();
        partner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(partner))
            )
            .andExpect(status().isBadRequest());

        // Validate the Partner in the database
        List<Partner> partnerList = partnerRepository.findAll();
        assertThat(partnerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPartner() throws Exception {
        int databaseSizeBeforeUpdate = partnerRepository.findAll().size();
        partner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(partner)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Partner in the database
        List<Partner> partnerList = partnerRepository.findAll();
        assertThat(partnerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePartnerWithPatch() throws Exception {
        // Initialize the database
        partnerRepository.saveAndFlush(partner);

        int databaseSizeBeforeUpdate = partnerRepository.findAll().size();

        // Update the partner using partial update
        Partner partialUpdatedPartner = new Partner();
        partialUpdatedPartner.setId(partner.getId());

        partialUpdatedPartner.address(UPDATED_ADDRESS).gstNumber(UPDATED_GST_NUMBER);

        restPartnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartner.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPartner))
            )
            .andExpect(status().isOk());

        // Validate the Partner in the database
        List<Partner> partnerList = partnerRepository.findAll();
        assertThat(partnerList).hasSize(databaseSizeBeforeUpdate);
        Partner testPartner = partnerList.get(partnerList.size() - 1);
        assertThat(testPartner.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPartner.getCompanyName()).isEqualTo(DEFAULT_COMPANY_NAME);
        assertThat(testPartner.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testPartner.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testPartner.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testPartner.getPreferredContactTime()).isEqualTo(DEFAULT_PREFERRED_CONTACT_TIME);
        assertThat(testPartner.getGstNumber()).isEqualTo(UPDATED_GST_NUMBER);
        assertThat(testPartner.getPanNumber()).isEqualTo(DEFAULT_PAN_NUMBER);
    }

    @Test
    @Transactional
    void fullUpdatePartnerWithPatch() throws Exception {
        // Initialize the database
        partnerRepository.saveAndFlush(partner);

        int databaseSizeBeforeUpdate = partnerRepository.findAll().size();

        // Update the partner using partial update
        Partner partialUpdatedPartner = new Partner();
        partialUpdatedPartner.setId(partner.getId());

        partialUpdatedPartner
            .name(UPDATED_NAME)
            .companyName(UPDATED_COMPANY_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .preferredContactTime(UPDATED_PREFERRED_CONTACT_TIME)
            .gstNumber(UPDATED_GST_NUMBER)
            .panNumber(UPDATED_PAN_NUMBER);

        restPartnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartner.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPartner))
            )
            .andExpect(status().isOk());

        // Validate the Partner in the database
        List<Partner> partnerList = partnerRepository.findAll();
        assertThat(partnerList).hasSize(databaseSizeBeforeUpdate);
        Partner testPartner = partnerList.get(partnerList.size() - 1);
        assertThat(testPartner.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPartner.getCompanyName()).isEqualTo(UPDATED_COMPANY_NAME);
        assertThat(testPartner.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testPartner.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testPartner.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testPartner.getPreferredContactTime()).isEqualTo(UPDATED_PREFERRED_CONTACT_TIME);
        assertThat(testPartner.getGstNumber()).isEqualTo(UPDATED_GST_NUMBER);
        assertThat(testPartner.getPanNumber()).isEqualTo(UPDATED_PAN_NUMBER);
    }

    @Test
    @Transactional
    void patchNonExistingPartner() throws Exception {
        int databaseSizeBeforeUpdate = partnerRepository.findAll().size();
        partner.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partner.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partner))
            )
            .andExpect(status().isBadRequest());

        // Validate the Partner in the database
        List<Partner> partnerList = partnerRepository.findAll();
        assertThat(partnerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPartner() throws Exception {
        int databaseSizeBeforeUpdate = partnerRepository.findAll().size();
        partner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partner))
            )
            .andExpect(status().isBadRequest());

        // Validate the Partner in the database
        List<Partner> partnerList = partnerRepository.findAll();
        assertThat(partnerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPartner() throws Exception {
        int databaseSizeBeforeUpdate = partnerRepository.findAll().size();
        partner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(partner)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Partner in the database
        List<Partner> partnerList = partnerRepository.findAll();
        assertThat(partnerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePartner() throws Exception {
        // Initialize the database
        partnerRepository.saveAndFlush(partner);

        int databaseSizeBeforeDelete = partnerRepository.findAll().size();

        // Delete the partner
        restPartnerMockMvc
            .perform(delete(ENTITY_API_URL_ID, partner.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Partner> partnerList = partnerRepository.findAll();
        assertThat(partnerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

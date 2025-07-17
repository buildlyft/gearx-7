package com.gearx7.app.web.rest;

import com.gearx7.app.domain.Partner;
import com.gearx7.app.repository.PartnerRepository;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.gearx7.app.domain.Partner}.
 */
@RestController
@RequestMapping("/api/partners")
@Transactional
public class PartnerResource {

    private final Logger log = LoggerFactory.getLogger(PartnerResource.class);

    private static final String ENTITY_NAME = "partner";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PartnerRepository partnerRepository;

    public PartnerResource(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    /**
     * {@code POST  /partners} : Create a new partner.
     *
     * @param partner the partner to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new partner, or with status {@code 400 (Bad Request)} if the partner has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Partner> createPartner(@Valid @RequestBody Partner partner) throws URISyntaxException {
        log.debug("REST request to save Partner : {}", partner);
        if (partner.getId() != null) {
            throw new BadRequestAlertException("A new partner cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Partner result = partnerRepository.save(partner);
        return ResponseEntity
            .created(new URI("/api/partners/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /partners/:id} : Updates an existing partner.
     *
     * @param id the id of the partner to save.
     * @param partner the partner to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partner,
     * or with status {@code 400 (Bad Request)} if the partner is not valid,
     * or with status {@code 500 (Internal Server Error)} if the partner couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Partner> updatePartner(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Partner partner
    ) throws URISyntaxException {
        log.debug("REST request to update Partner : {}, {}", id, partner);
        if (partner.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partner.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partnerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Partner result = partnerRepository.save(partner);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, partner.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /partners/:id} : Partial updates given fields of an existing partner, field will ignore if it is null
     *
     * @param id the id of the partner to save.
     * @param partner the partner to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partner,
     * or with status {@code 400 (Bad Request)} if the partner is not valid,
     * or with status {@code 404 (Not Found)} if the partner is not found,
     * or with status {@code 500 (Internal Server Error)} if the partner couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Partner> partialUpdatePartner(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Partner partner
    ) throws URISyntaxException {
        log.debug("REST request to partial update Partner partially : {}, {}", id, partner);
        if (partner.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partner.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partnerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Partner> result = partnerRepository
            .findById(partner.getId())
            .map(existingPartner -> {
                if (partner.getName() != null) {
                    existingPartner.setName(partner.getName());
                }
                if (partner.getCompanyName() != null) {
                    existingPartner.setCompanyName(partner.getCompanyName());
                }
                if (partner.getEmail() != null) {
                    existingPartner.setEmail(partner.getEmail());
                }
                if (partner.getPhone() != null) {
                    existingPartner.setPhone(partner.getPhone());
                }
                if (partner.getAddress() != null) {
                    existingPartner.setAddress(partner.getAddress());
                }
                if (partner.getPreferredContactTime() != null) {
                    existingPartner.setPreferredContactTime(partner.getPreferredContactTime());
                }
                if (partner.getGstNumber() != null) {
                    existingPartner.setGstNumber(partner.getGstNumber());
                }
                if (partner.getPanNumber() != null) {
                    existingPartner.setPanNumber(partner.getPanNumber());
                }

                return existingPartner;
            })
            .map(partnerRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, partner.getId().toString())
        );
    }

    /**
     * {@code GET  /partners} : get all the partners.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of partners in body.
     */
    @GetMapping("")
    public List<Partner> getAllPartners() {
        log.debug("REST request to get all Partners");
        return partnerRepository.findAll();
    }

    /**
     * {@code GET  /partners/:id} : get the "id" partner.
     *
     * @param id the id of the partner to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the partner, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Partner> getPartner(@PathVariable("id") Long id) {
        log.debug("REST request to get Partner : {}", id);
        Optional<Partner> partner = partnerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(partner);
    }

    /**
     * {@code DELETE  /partners/:id} : delete the "id" partner.
     *
     * @param id the id of the partner to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartner(@PathVariable("id") Long id) {
        log.debug("REST request to delete Partner : {}", id);
        partnerRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

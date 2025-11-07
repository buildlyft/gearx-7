package com.gearx7.app.service;

import com.gearx7.app.domain.Subcategory;
import com.gearx7.app.repository.SubcategoryRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gearx7.app.domain.Subcategory}.
 */
@Service
@Transactional
public class SubcategoryService {

    private final Logger log = LoggerFactory.getLogger(SubcategoryService.class);

    private final SubcategoryRepository subcategoryRepository;

    public SubcategoryService(SubcategoryRepository subcategoryRepository) {
        this.subcategoryRepository = subcategoryRepository;
    }

    /**
     * Save a subcategory.
     *
     * @param subcategory the entity to save.
     * @return the persisted entity.
     */
    public Subcategory save(Subcategory subcategory) {
        log.debug("Request to save Subcategory : {}", subcategory);
        return subcategoryRepository.save(subcategory);
    }

    /**
     * Update a subcategory.
     *
     * @param subcategory the entity to save.
     * @return the persisted entity.
     */
    public Subcategory update(Subcategory subcategory) {
        log.debug("Request to update Subcategory : {}", subcategory);
        return subcategoryRepository.save(subcategory);
    }

    /**
     * Partially update a subcategory.
     *
     * @param subcategory the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Subcategory> partialUpdate(Subcategory subcategory) {
        log.debug("Request to partially update Subcategory : {}", subcategory);

        return subcategoryRepository
            .findById(subcategory.getId())
            .map(existingSubcategory -> {
                if (subcategory.getName() != null) {
                    existingSubcategory.setName(subcategory.getName());
                }
                if (subcategory.getDescription() != null) {
                    existingSubcategory.setDescription(subcategory.getDescription());
                }
                if (subcategory.getImage() != null) {
                    existingSubcategory.setImage(subcategory.getImage());
                }
                if (subcategory.getImageContentType() != null) {
                    existingSubcategory.setImageContentType(subcategory.getImageContentType());
                }

                return existingSubcategory;
            })
            .map(subcategoryRepository::save);
    }

    /**
     * Get all the subcategories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Subcategory> findAll(Pageable pageable) {
        log.debug("Request to get all Subcategories");
        return subcategoryRepository.findAll(pageable);
    }

    /**
     * Get one subcategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Subcategory> findOne(Long id) {
        log.debug("Request to get Subcategory : {}", id);
        return subcategoryRepository.findById(id);
    }

    /**
     * Delete the subcategory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Subcategory : {}", id);
        subcategoryRepository.deleteById(id);
    }
}

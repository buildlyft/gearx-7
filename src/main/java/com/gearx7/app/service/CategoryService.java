package com.gearx7.app.service;

import com.gearx7.app.domain.Category;
import com.gearx7.app.domain.Type;
import com.gearx7.app.repository.CategoryRepository;
import com.gearx7.app.repository.TypeRepository;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gearx7.app.domain.Category}.
 */
@Service
@Transactional
public class CategoryService {

    private final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    private final TypeRepository typeRepository;

    public CategoryService(CategoryRepository categoryRepository, TypeRepository typeRepository) {
        this.categoryRepository = categoryRepository;
        this.typeRepository = typeRepository;
    }

    /**
     * Save a category.
     *
     * @param category the entity to save.
     * @return the persisted entity.
     */
    public Category save(Category category) {
        log.debug("Request to save Category : {}", category);
        category.setType(resolveType(category));
        return categoryRepository.save(category);
    }

    /**
     * Update a category.
     *
     * @param category the entity to save.
     * @return the persisted entity.
     */
    public Category update(Category category) {
        log.debug("Request to update Category : {}", category);
        // Ensure category exists before updating
        // if exists retrieve existing entity and if not found throw error
        Category existing = getCategoryOrThrow(category.getId());
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        existing.setImage(category.getImage());
        existing.setImageContentType(category.getImageContentType());
        existing.setType(resolveType(category));

        return categoryRepository.save(existing);
    }

    /**
     * Partially update a category.
     *
     * @param category the entity to update partially.
     * @return the persisted entity.
     */
    public Category partialUpdate(Category category) {
        log.debug("Request to partially update Category : {}", category);

        Category existing = getCategoryOrThrow(category.getId());

        if (category.getName() != null) {
            existing.setName(category.getName());
        }
        if (category.getDescription() != null) {
            existing.setDescription(category.getDescription());
        }
        if (category.getImage() != null) {
            existing.setImage(category.getImage());
        }
        if (category.getImageContentType() != null) {
            existing.setImageContentType(category.getImageContentType());
        }
        if (category.getType() != null) {
            existing.setType(resolveType(category));
        }

        return categoryRepository.save(existing);
    }

    /**
     * Get all the categories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Category> findAll(Pageable pageable) {
        log.debug("Request to get all Categories");
        return categoryRepository.findAll(pageable);
    }

    /**
     * Get one category by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Category findOne(Long id) {
        log.debug("Request to get Category : {}", id);
        return getCategoryOrThrow(id);
    }

    /**
     * Delete the category by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Category : {}", id);
        Category existing = getCategoryOrThrow(id);
        categoryRepository.delete(existing);
    }

    //=============Helper methods================

    /**
     * Resolve and validate Type entity.
     * Ensures managed entity is attached to persistence context.
     */

    private Type resolveType(Category category) {
        if (category.getType() == null || category.getType().getId() == null) {
            return null;
        }

        return typeRepository
            .findById(category.getType().getId())
            .orElseThrow(() -> new BadRequestAlertException("Invalid Type ID", "category", "typenotfound"));
    }

    private Category getCategoryOrThrow(Long id) {
        if (id == null) {
            throw new BadRequestAlertException("Category ID is required", "category", "idnull");
        }

        return categoryRepository
            .findByIdWithSubcategories(id)
            .orElseThrow(() -> new BadRequestAlertException("Category not found", "category", "idnotfound"));
    }
}

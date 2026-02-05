package com.gearx7.app.service;

import com.gearx7.app.domain.Category;
import com.gearx7.app.domain.Type;
import com.gearx7.app.repository.CategoryRepository;
import com.gearx7.app.repository.TypeRepository;
import com.gearx7.app.service.dto.CategoryDTO;
import com.gearx7.app.service.interfaces.DocumentStorageService;
import com.gearx7.app.service.mapper.CategoryMapper;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import com.gearx7.app.web.rest.errors.NotFoundAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing {@link com.gearx7.app.domain.Category}.
 */
@Service
@Transactional
public class CategoryService {

    private final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    private final TypeRepository typeRepository;

    private final DocumentStorageService documentStorageService;

    private final CategoryMapper categoryMapper;

    public CategoryService(
        CategoryRepository categoryRepository,
        TypeRepository typeRepository,
        DocumentStorageService documentStorageService,
        CategoryMapper categoryMapper
    ) {
        this.categoryRepository = categoryRepository;
        this.typeRepository = typeRepository;
        this.documentStorageService = documentStorageService;
        this.categoryMapper = categoryMapper;
    }

    /**
     * Save a category.
     *
     * @param categoryDTO the entity to save.
     * @param image the image file to upload.
     * @return the persisted entity.
     */
    public CategoryDTO save(CategoryDTO categoryDTO, MultipartFile image) {
        log.debug("Request to save Category : {}", categoryDTO);

        if (categoryRepository.existsByNameIgnoreCase(categoryDTO.getName())) {
            throw new BadRequestAlertException("Category already exists", "category", "Category already exists");
        }

        resolveType(categoryDTO.getTypeId());

        // Step 1: Save temporarily to generate ID
        categoryDTO.setImageUrl("temp");
        Category category = categoryMapper.toEntity(categoryDTO);
        Category saved = categoryRepository.save(category);

        String imageUrl = documentStorageService.uploadCategoryImage(image, saved.getId());

        // Step 3: Update with real URL
        saved.setImageUrl(imageUrl);

        Category finalCategory = categoryRepository.save(saved);

        log.info("Category created successfully | id={}", finalCategory.getId());

        return categoryMapper.toDto(saved);
    }

    /**
     * Update a category.
     *
     * @param categoryDTO the entity to save.
     * @Param MultiPartFile
     * @return the persisted entity.
     */
    public CategoryDTO update(CategoryDTO categoryDTO, MultipartFile file) {
        log.debug("Request to update Category : {}", categoryDTO);
        // Ensure category exists before updating
        // if exists retrieve existing entity and if not found throw error
        Category existing = getCategoryOrThrow(categoryDTO.getId());
        existing.setName(categoryDTO.getName());
        existing.setDescription(categoryDTO.getDescription());
        existing.setType(resolveType(categoryDTO.getTypeId()));

        if (file != null && !file.isEmpty()) {
            String imageUrl = documentStorageService.uploadCategoryImage(file, existing.getId());
            existing.setImageUrl(imageUrl);
        }

        Category savedCategory = categoryRepository.save(existing);
        return categoryMapper.toDto(savedCategory);
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
            existing.setType(resolveType(category.getId()));
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
    public Page<CategoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Categories");
        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }

    /**
     * Get one category by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public CategoryDTO findOne(Long id) {
        log.debug("Request to get Category : {}", id);
        Category category = getCategoryOrThrow(id);
        return categoryMapper.toDto(category);
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

    private Type resolveType(Long id) {
        return typeRepository.findById(id).orElseThrow(() -> new NotFoundAlertException("Invalid Type ID", "category", "TypeIdNotFound"));
    }

    private Category getCategoryOrThrow(Long id) {
        if (id == null) {
            throw new BadRequestAlertException("Category ID is required", "category", "CategoryIdNull");
        }

        return categoryRepository
            .findByIdWithSubcategories(id)
            .orElseThrow(() -> new NotFoundAlertException("Category not found with given id :" + id, "category", "CategoryNotFound" + ""));
    }
}

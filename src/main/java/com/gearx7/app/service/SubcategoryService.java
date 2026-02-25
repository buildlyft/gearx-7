package com.gearx7.app.service;

import com.gearx7.app.domain.Category;
import com.gearx7.app.domain.Subcategory;
import com.gearx7.app.repository.CategoryRepository;
import com.gearx7.app.repository.SubcategoryRepository;
import com.gearx7.app.service.dto.SubCategoryDTO;
import com.gearx7.app.service.interfaces.DocumentStorageService;
import com.gearx7.app.service.mapper.SubcategoryMapper;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import com.gearx7.app.web.rest.errors.NotFoundAlertException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing {@link com.gearx7.app.domain.Subcategory}.
 */
@Service
@Transactional
public class SubcategoryService {

    private final Logger log = LoggerFactory.getLogger(SubcategoryService.class);

    private final SubcategoryRepository subcategoryRepository;

    private final CategoryRepository categoryRepository;

    private final DocumentStorageService documentStorageService;

    private final SubcategoryMapper subcategoryMapper;

    public SubcategoryService(
        SubcategoryRepository subcategoryRepository,
        CategoryRepository categoryRepository,
        DocumentStorageService documentStorageService,
        SubcategoryMapper subcategoryMapper
    ) {
        this.subcategoryRepository = subcategoryRepository;
        this.categoryRepository = categoryRepository;
        this.documentStorageService = documentStorageService;
        this.subcategoryMapper = subcategoryMapper;
    }

    /**
     * Save a subcategory.
     *
     * @param subcategoryDTO the entity to save.
     * @param image the image file to upload.
     *
     * @return the persisted entity.
     */
    public SubCategoryDTO save(SubCategoryDTO subcategoryDTO, MultipartFile image) {
        log.debug("Request to save Subcategory : {}", subcategoryDTO);
        if (image == null || image.isEmpty()) {
            throw new BadRequestAlertException("Subcategory image is required", "subcategory", "Image Required");
        }
        Category category = categoryRepository
            .findById(subcategoryDTO.getCategoryId())
            .orElseThrow(() -> new NotFoundAlertException("Category not found", "subcategory", "CategoryNotFound"));

        subcategoryDTO.setCategoryId(category.getId());
        Subcategory subcategory = subcategoryMapper.toEntity(subcategoryDTO);
        Subcategory saved = subcategoryRepository.save(subcategory);
        String imageUrl = documentStorageService.uploadSubcategoryImage(image, saved.getId());
        saved.setImageUrl(imageUrl);
        Subcategory finalSaved = subcategoryRepository.save(saved);
        log.info("Subcategory created successfully with ID: {}", finalSaved.getId());
        return subcategoryMapper.toDto(finalSaved);
    }

    /**
     * Update a subcategory.
     *
     * @param subCategoryDTO the entity to save.
     * @param image the image file to upload.
     * @return the persisted entity.
     */
    public SubCategoryDTO update(SubCategoryDTO subCategoryDTO, MultipartFile image) {
        log.debug("Request to update Subcategory : {}", subCategoryDTO);

        Subcategory existing = getSubcategoryOrThrow(subCategoryDTO.getId());
        existing.setName(subCategoryDTO.getName());
        existing.setDescription(subCategoryDTO.getDescription());

        Category existigSubcategory = getCategoryOrThrow(subCategoryDTO.getCategoryId());
        existing.setCategory(existigSubcategory);

        // Update image only if new file provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = documentStorageService.uploadSubcategoryImage(image, existing.getId());

            existing.setImageUrl(imageUrl);
        }

        Subcategory saved = subcategoryRepository.save(existing);

        return subcategoryMapper.toDto(saved);
    }

    /**
     * Partially update a subcategory.
     *
     * @param dto the entity to save.
     * @Param image the image file to upload.
     * @return the persisted entity.
     */
    public SubCategoryDTO partialUpdate(SubCategoryDTO dto, MultipartFile image) {
        log.debug("Request to partially update Subcategory : {}", dto);
        Subcategory existing = getSubcategoryOrThrow(dto.getId());
        log.debug("Existing subcategory fetched | id={} | currentState={}", existing.getId(), existing);

        if (dto.getName() != null) {
            log.debug("Updating name | old={} | new={}", existing.getName(), dto.getName());
            existing.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            log.debug("Updating description");
            existing.setDescription(dto.getDescription());
        }
        if (dto.getCategoryId() != null) {
            log.debug("Updating category | newCategoryId={}", dto.getCategoryId());
            Category category = getCategoryOrThrow(dto.getCategoryId());
            existing.setCategory(category);
        }
        if (image != null && !image.isEmpty()) {
            log.info("Uploading new image for subcategory | id={}", existing.getId());
            String imageUrl = documentStorageService.uploadSubcategoryImage(image, existing.getId());
            existing.setImageUrl(imageUrl);
            log.debug("Image updated | newUrl={}", imageUrl);
        }
        Subcategory saved = subcategoryRepository.save(existing);

        log.info("Partial update completed successfully | id={}", saved.getId());

        return subcategoryMapper.toDto(saved);
    }

    /**
     * Get all the subcategories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SubCategoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Subcategories");

        return subcategoryRepository.findAll(pageable).map(subcategoryMapper::toDto);
    }

    /**
     * Get one subcategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SubCategoryDTO> findOne(Long id) {
        log.debug("Request to get Subcategory : {}", id);
        return subcategoryRepository.findById(id).map(subcategoryMapper::toDto);
    }

    /**
     * Delete the subcategory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Subcategory : {}", id);
        Subcategory subcategory = getSubcategoryOrThrow(id);
        subcategoryRepository.deleteById(subcategory.getId());
    }

    private Subcategory getSubcategoryOrThrow(Long id) {
        return subcategoryRepository
            .findById(id)
            .orElseThrow(() ->
                new NotFoundAlertException("Subcategory not found with given id: " + id, "subcategory", "SubcategoryIdNotFound")
            );
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundAlertException("Category not found with given id: " + id, "category", "CategoryIdNotFound"));
    }
}

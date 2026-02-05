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
                if (subcategory.getCategory().getId() != null) {
                    Category category = getCategoryOrThrow(subcategory.getCategory().getId());
                    existingSubcategory.setCategory(category);
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

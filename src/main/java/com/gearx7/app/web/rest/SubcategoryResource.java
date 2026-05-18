package com.gearx7.app.web.rest;

import com.gearx7.app.domain.Subcategory;
import com.gearx7.app.repository.SubcategoryRepository;
import com.gearx7.app.service.SubcategoryService;
import com.gearx7.app.service.dto.ApiResponse;
import com.gearx7.app.service.dto.SubCategoryDTO;
import com.gearx7.app.service.interfaces.DocumentStorageService;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import com.gearx7.app.web.rest.errors.NotFoundAlertException;
import jakarta.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

/**
 * REST controller for managing {@link Subcategory}.
 */
@RestController
@RequestMapping("/api/subcategories")
public class SubcategoryResource {

    private final Logger log = LoggerFactory.getLogger(SubcategoryResource.class);

    private static final String ENTITY_NAME = "subcategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubcategoryService subcategoryService;

    private final SubcategoryRepository subcategoryRepository;

    private final DocumentStorageService documentStorageService;

    public SubcategoryResource(
        SubcategoryService subcategoryService,
        SubcategoryRepository subcategoryRepository,
        DocumentStorageService documentStorageService
    ) {
        this.subcategoryService = subcategoryService;
        this.subcategoryRepository = subcategoryRepository;
        this.documentStorageService = documentStorageService;
    }

    /**
     * {@code POST  /subcategories} : Create a new subcategory.
     *
     * @param subCategoryDTO the subcategory to create.
     * @param image the image file to upload.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subcategory, or with status {@code 400 (Bad Request)} if the subcategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SubCategoryDTO>> createSubcategory(
        @Valid @RequestPart("subcategory") SubCategoryDTO subCategoryDTO,
        @RequestPart("file") MultipartFile image
    ) throws URISyntaxException {
        log.debug("REST request to save Subcategory : {}", subCategoryDTO);
        if (subCategoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new subcategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SubCategoryDTO result = subcategoryService.save(subCategoryDTO, image);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(), "Subcategory created successfully with id " + result.getId(), result)
            );
    }

    /**
     * {@code PUT  /subcategories/:id} : Updates an existing subcategory.
     *
     * @param id the id of the subcategory to save.
     * @param subcategoryDTO the subcategory to update.
     * @param image the image file to upload.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subcategory,
     * or with status {@code 400 (Bad Request)} if the subcategory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subcategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SubCategoryDTO>> updateSubcategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestPart("subcategory") SubCategoryDTO subcategoryDTO,
        @RequestPart(value = "file", required = false) MultipartFile image
    ) throws URISyntaxException {
        log.debug("REST request to update Subcategory : {}, {}", id, subcategoryDTO);
        if (subcategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subcategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        SubCategoryDTO result = subcategoryService.update(subcategoryDTO, image);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Subcategory updated successfully", result));
    }

    /**
     * {@code PATCH  /subcategories/:id} : Partial updates given fields of an existing subcategory, field will ignore if it is null
     *
     * @param id the id of the subcategory to save.
     * @param subCategoryDTO the subcategory to update.
     * @param image the image file to upload.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subcategory,
     * or with status {@code 400 (Bad Request)} if the subcategory is not valid,
     * or with status {@code 404 (Not Found)} if the subcategory is not found,
     * or with status {@code 500 (Internal Server Error)} if the subcategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SubCategoryDTO>> partialUpdateSubcategory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestPart("subcategory") SubCategoryDTO subCategoryDTO,
        @RequestPart(value = "file", required = false) MultipartFile image
    ) throws URISyntaxException {
        log.debug("REST request to partial update Subcategory partially : {}, {}", id, subCategoryDTO);
        if (subCategoryDTO.getId() == null) {
            log.warn("PATCH failed: Subcategory ID is null");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "IdNull");
        }
        if (!Objects.equals(id, subCategoryDTO.getId())) {
            log.warn("PATCH failed: Path ID {} does not match DTO ID {}", id, subCategoryDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "IdInvalid");
        }

        SubCategoryDTO result = subcategoryService.partialUpdate(subCategoryDTO, image);

        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Subcategory partially updated successfully", result));
    }

    /**
     * {@code GET  /subcategories} : get all the subcategories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subcategories in body.
     */
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<SubCategoryDTO>>> getAllSubcategories(@ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Subcategories");
        Page<SubCategoryDTO> page = subcategoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok(
            new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                page.getContent().isEmpty() ? "Subcategories data not available" : "Subcategories data fetched successfully",
                page.getContent()
            )
        );
    }

    /**
     * {@code GET  /subcategories/:id} : get the "id" subcategory.
     *
     * @param id the id of the subcategory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subcategory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubCategoryDTO>> getSubcategory(@PathVariable("id") Long id) {
        log.debug("REST request to get Subcategory : {}", id);
        SubCategoryDTO subcategory = subcategoryService
            .findOne(id)
            .orElseThrow(() -> new NotFoundAlertException("Subcategory not found with given id : " + id, ENTITY_NAME, "SubcategoryNotFound")
            );
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Subcategory data fetched successfully", subcategory));
    }

    /**
     * {@code DELETE  /subcategories/:id} : delete the "id" subcategory.
     *
     * @param id the id of the subcategory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubcategory(@PathVariable("id") Long id) {
        log.debug("REST request to delete Subcategory : {}", id);
        subcategoryService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Subcategory deleted successfully", null));
    }
}

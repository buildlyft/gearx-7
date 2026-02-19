package com.gearx7.app.web.rest;

import com.gearx7.app.domain.Category;
import com.gearx7.app.domain.Type;
import com.gearx7.app.repository.TypeRepository;
import com.gearx7.app.service.interfaces.TypeService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/types")
public class TypeResource {

    private final Logger log = LoggerFactory.getLogger(TypeResource.class);

    private final TypeService typeService;

    private final TypeRepository typeRepository;

    public TypeResource(TypeService typeService, TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
        this.typeService = typeService;
    }

    /**
     * API endpoints for Type management will be implemented here.
     */

    /* ================= CREATE Type (ADMIN ONLY) ================= */

    /**
     * {@code POST  /types} : Create a new Type.
     *
     * @param type the Type to create.
     * @return the {@link ResponseEntity} with status
     * {@code 201 (Created)} and with body the new Type, or with status
     * {@code 400 (Bad Request)} if the Type has already an ID.
     */
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Type> createType(@Valid @RequestPart("type") Type type, @RequestPart("image") MultipartFile image) {
        log.info("REST request to Create Type : {}", type);
        Type result = typeService.createType(type, image);
        log.info("Type is created successfully with id : {}", result.getId());
        return ResponseEntity.created(URI.create("/api/types/" + result.getId())).body(result);
    }

    /**
     * Get all Types.
     * @return List of Types
     */

    @GetMapping("")
    public ResponseEntity<List<Type>> getAllTypes() {
        log.info("REST request to get all Types");
        List<Type> types = typeService.getAllTypes();
        log.info("Returning {} types", types.size());
        return ResponseEntity.ok(types);
    }

    /**
     * Get Type by ID.
     * @param id
     * if found return Type else throw 404 error
     */

    @GetMapping("/{id}")
    public ResponseEntity<Type> getTypeById(@PathVariable(required = true, name = "id") Long id) {
        log.info("REST request to get Type by id : {}", id);
        Type type = typeService.getTypeById(id);
        log.info("Fetched Type : {}", type);
        return ResponseEntity.ok(type);
    }

    @GetMapping("/{typeId}/categories")
    public ResponseEntity<List<Category>> getCategoriesByTypeId(@PathVariable("typeId") Long typeId) {
        log.info("REST request to get Categories for Type id : {}", typeId);
        List<Category> categories = typeService.getCategoriesByTypeId(typeId);
        log.info("Returning {} categories for Type id : {}", categories.size(), typeId);
        return ResponseEntity.ok(categories);
    }

    /**
     * ================= UPDATE Type (ADMIN ONLY) =================
     * @param type
     * if given type is valid update and return updated type else throw error
     */

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Type> updateType(
        @PathVariable(required = true, name = "id") Long id,
        @Valid @RequestPart("type") Type type,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        log.info("REST request to update Type with id {} : {} ", id, type);
        Type updatedType = typeService.updateType(id, type, image);
        log.info("Type updated successfully with id: {}", id);
        return ResponseEntity.ok(updatedType);
    }

    /**
     * ================= DELETE Type (ADMIN ONLY) =================
     * @param id
     * delete type if exists else throw error
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteType(@PathVariable(required = true, name = "id") Long id) {
        log.info("REST request to delete Type with id : {}", id);
        typeService.deleteType(id);
        log.info("Type deleted successfully with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}

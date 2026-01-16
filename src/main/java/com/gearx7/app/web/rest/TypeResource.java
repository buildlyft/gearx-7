package com.gearx7.app.web.rest;

import com.gearx7.app.domain.Type;
import com.gearx7.app.service.interfaces.TypeService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/types")
public class TypeResource {

    private final TypeService typeService;

    public TypeResource(TypeService typeService) {
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
    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Type> createType(@Valid @RequestBody Type type) {
        Type result = typeService.createType(type);
        return ResponseEntity.created(URI.create("/api/types/" + result.getId())).body(result);
    }

    /**
     * Get all Types.
     * @return List of Types
     */

    @GetMapping("")
    public ResponseEntity<List<Type>> getAllTypes() {
        List<Type> types = typeService.getAllTypes();

        return ResponseEntity.ok(types);
    }

    /**
     * Get Type by ID.
     * @param id
     * if found return Type else throw 404 error
     */

    @GetMapping("/{id}")
    public ResponseEntity<Type> getTypeById(@PathVariable(required = true, name = "id") Long id) {
        Type type = typeService.getTypeById(id);
        return ResponseEntity.ok(type);
    }

    /**
     * ================= UPDATE Type (ADMIN ONLY) =================
     * @param type
     * if given type is valid update and return updated type else throw error
     */

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Type> updateType(@PathVariable(required = true, name = "id") Long id, @Valid @RequestBody Type type) {
        return ResponseEntity.ok(typeService.updateType(id, type));
    }

    /**
     * ================= DELETE Type (ADMIN ONLY) =================
     * @param id
     * delete type if exists else throw error
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteType(@PathVariable(required = true, name = "id") Long id) {
        typeService.deleteType(id);
        return ResponseEntity.noContent().build();
    }
}

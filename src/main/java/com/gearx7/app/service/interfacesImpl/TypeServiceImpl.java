package com.gearx7.app.service.interfacesImpl;

import com.gearx7.app.domain.Category;
import com.gearx7.app.domain.Type;
import com.gearx7.app.repository.CategoryRepository;
import com.gearx7.app.repository.TypeRepository;
import com.gearx7.app.service.interfaces.TypeService;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import com.gearx7.app.web.rest.errors.NotFoundAlertException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TypeServiceImpl implements TypeService {

    private final Logger log = LoggerFactory.getLogger(TypeServiceImpl.class);

    private final TypeRepository typeRepository;

    private final CategoryRepository categoryRepository;

    public TypeServiceImpl(TypeRepository typeRepository, CategoryRepository categoryRepository) {
        this.typeRepository = typeRepository;
        this.categoryRepository = categoryRepository;
    }

    /**Create a new Type
     *
     * @param type
     * @return Type
     *
     */

    @Override
    public Type createType(Type type) {
        log.info("Request to create Type: {}", type);
        if (type.getId() != null) {
            log.error("Attempted to create a new Type with existing ID: {}", type.getId());
            throw new BadRequestAlertException("New type cannot already have an ID", "type", "idexists");
        }

        String normalized = normalize(type.getTypeName());

        if (normalized.isEmpty()) {
            throw new BadRequestAlertException("Type name cannot be blank", "type", "blank");
        }

        if (typeRepository.existsByTypeNameIgnoreCase(normalized)) {
            log.error("Attempted to create a duplicate Type with name: {}", type.getTypeName());
            throw new BadRequestAlertException("Type already exists", "type", "duplicate");
        }
        type.setTypeName(normalized);
        Type savedType = typeRepository.save(type);
        log.info("Type created successfully with ID: {}", savedType.getId());
        return savedType;
    }

    /**
     * Update an existing Type
     * @param type
     * @return Persisted(Updated) Type
     */

    @Override
    public Type updateType(Long id, Type type) {
        log.info("Request to update Type with ID {} : {}", id, type);

        if (type.getId() != null && !type.getId().equals(id)) {
            throw new BadRequestAlertException("Invalid ID", "type", "idinvalid");
        }

        Type existingType = getTypeById(id);

        String existingName = normalize(existingType.getTypeName());
        String newName = normalize(type.getTypeName());

        if (newName.isEmpty()) {
            throw new BadRequestAlertException("Type name cannot be blank", "type", "blank");
        }

        if (existingName.equalsIgnoreCase(newName)) {
            log.info("No update needed. Type name unchanged for id {}", id);
            return existingType;
        }

        // DUPLICATE CHECK
        if (typeRepository.existsByTypeNameIgnoreCase(newName)) {
            log.error("Duplicate Type name found during update: {}", type.getTypeName());
            throw new BadRequestAlertException("Type already exists", "type", "duplicate");
        }
        existingType.setTypeName(newName);

        Type updatedType = typeRepository.save(existingType);
        log.info("Type with ID {} updated successfully", existingType.getId());

        return updatedType;
    }

    /**
     * Get all Types
     * @return List of Types
     */
    @Override
    @Transactional(readOnly = true)
    public List<Type> getAllTypes() {
        log.info("Request to get all Types");
        return typeRepository.findAllWithCategories();
    }

    @Override
    public void deleteType(Long id) {
        log.info("Request to delete Type with id {}", id);
        Type existing = getTypeById(id);

        if (!existing.getCategories().isEmpty()) {
            throw new BadRequestAlertException("Cannot delete type with existing categories", "type", "hascategories");
        }
        typeRepository.delete(existing);
        log.info("Type with id {} deleted successfully", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoriesByTypeId(Long typeId) {
        log.debug("Request to get Categories for Type id: {}", typeId);

        List<Category> categories = categoryRepository.findByTypeId(typeId);

        if (categories.isEmpty() && !typeRepository.existsById(typeId)) {
            throw new NotFoundAlertException("Type not found with given id: " + typeId, "type", "TypeIdNotFound");
        }

        return categories;
    }

    /**
     * Get Type by id
     * @param id
     * if exists from Db @return Type or else throw exception
     */
    @Override
    @Transactional(readOnly = true)
    public Type getTypeById(Long id) {
        log.debug("Request to get Type with id {}", id);

        return typeRepository
            .findByIdWithCategories(id)
            .orElseThrow(() -> {
                log.error("Type not found with id {}", id);
                throw new NotFoundAlertException("Type not found with given id: " + id, "type", "TypeIdNotFound");
            });
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}

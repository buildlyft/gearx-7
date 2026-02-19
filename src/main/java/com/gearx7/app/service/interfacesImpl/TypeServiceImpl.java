package com.gearx7.app.service.interfacesImpl;

import com.gearx7.app.domain.Category;
import com.gearx7.app.domain.Type;
import com.gearx7.app.repository.CategoryRepository;
import com.gearx7.app.repository.TypeRepository;
import com.gearx7.app.service.interfaces.DocumentStorageService;
import com.gearx7.app.service.interfaces.TypeService;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import com.gearx7.app.web.rest.errors.NotFoundAlertException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class TypeServiceImpl implements TypeService {

    private final Logger log = LoggerFactory.getLogger(TypeServiceImpl.class);

    private final TypeRepository typeRepository;

    private final CategoryRepository categoryRepository;

    private final DocumentStorageService documentStorageService;

    public TypeServiceImpl(
        TypeRepository typeRepository,
        CategoryRepository categoryRepository,
        DocumentStorageService documentStorageService
    ) {
        this.typeRepository = typeRepository;
        this.categoryRepository = categoryRepository;
        this.documentStorageService = documentStorageService;
    }

    /**Create a new Type
     *
     * @param type
     * @return Type
     *
     */

    @Override
    public Type createType(Type type, MultipartFile file) {
        log.info("Request to create Type | name={}", type.getTypeName());
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

        if (file == null || file.isEmpty()) {
            throw new BadRequestAlertException("Image is required while creating type", "type", "imageRequired");
        }

        type.setTypeName(normalized);
        type.setImageUrl("temp");
        Type savedType = typeRepository.save(type);
        String imageUrl = documentStorageService.uploadTypeImage(file, savedType.getId());
        savedType.setImageUrl(imageUrl);
        Type finalType = typeRepository.save(savedType);
        log.info("Type created successfully | id={} | Name={}", finalType.getId(), finalType.getTypeName());
        return finalType;
    }

    /**
     * Update an existing Type
     * @param type
     * @return Persisted(Updated) Type
     */

    @Override
    public Type updateType(Long id, Type type, MultipartFile file) {
        log.info("Request to update Type with ID {} : {}", id, type);

        if (type.getId() != null && !type.getId().equals(id)) {
            throw new BadRequestAlertException("Invalid ID", "type", "idInvalid");
        }

        Type existingType = getTypeById(id);

        String existingName = normalize(existingType.getTypeName());
        String newName = normalize(type.getTypeName());

        if (newName.isEmpty()) {
            throw new BadRequestAlertException("Type name cannot be blank", "type", "blank");
        }

        // Only check duplicate if name is changed
        if (!existingName.equalsIgnoreCase(newName)) {
            if (typeRepository.existsByTypeNameIgnoreCase(newName)) {
                throw new BadRequestAlertException("Type already exists", "type", "duplicate");
            }

            existingType.setTypeName(newName);
        }

        if (file != null && !file.isEmpty()) {
            log.debug("Uploading new image for Type | id={}", id);
            String imageUrl = documentStorageService.uploadTypeImage(file, id);
            existingType.setImageUrl(imageUrl);
        }

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

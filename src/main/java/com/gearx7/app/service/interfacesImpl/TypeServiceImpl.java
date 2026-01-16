package com.gearx7.app.service.interfacesImpl;

import com.gearx7.app.domain.Type;
import com.gearx7.app.repository.TypeRepository;
import com.gearx7.app.service.interfaces.TypeService;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TypeServiceImpl implements TypeService {

    private final TypeRepository typeRepository;

    public TypeServiceImpl(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    /**Create a new Type
     *
     * @param type
     * @return Type
     *
     */

    @Override
    public Type createType(Type type) {
        if (type.getId() != null) {
            throw new BadRequestAlertException("New type cannot already have an ID", "type", "idexists");
        }

        if (typeRepository.existsByTypeNameIgnoreCase(type.getTypeName())) {
            throw new BadRequestAlertException("Type already exists", "type", "duplicate");
        }
        return typeRepository.save(type);
    }

    /**
     * Update an existing Type
     * @param type
     * @return Persisted(Updated) Type
     */

    @Override
    public Type updateType(Long id, Type type) {
        Type existingType = getTypeById(id);

        if (
            !existingType.getTypeName().equalsIgnoreCase(type.getTypeName()) &&
            typeRepository.existsByTypeNameIgnoreCase(type.getTypeName())
        ) {
            throw new BadRequestAlertException("Type already exists", "type", "duplicate");
        }
        existingType.setTypeName(type.getTypeName());

        return typeRepository.save(existingType);
    }

    /**
     * Get all Types
     * @return List of Types
     */
    @Override
    @Transactional(readOnly = true)
    public List<Type> getAllTypes() {
        return typeRepository.findAll();
    }

    /**
     * Get Type by Id
     * @param id
     * if exists from Db @return Type or else throw exception
     */
    @Override
    @Transactional
    public Type getTypeById(Long id) {
        return typeRepository.findById(id).orElseThrow(() -> new BadRequestAlertException("Type not found", "type", "notfound"));
    }

    @Override
    public void deleteType(Long id) {
        if (!typeRepository.existsById(id)) {
            throw new BadRequestAlertException("Type not found", "type", "notfound");
        }
        typeRepository.deleteById(id);
    }
}

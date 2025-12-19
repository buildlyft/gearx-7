package com.gearx7.app.service.mapper;

import com.gearx7.app.domain.Category;
import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.Subcategory;
import com.gearx7.app.domain.User;
import com.gearx7.app.service.dto.MachineDTO;
import com.gearx7.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Machine} and its DTO {@link MachineDTO}.
 */
@Mapper(componentModel = "spring")
public interface MachineMapper extends EntityMapper<MachineDTO, Machine> {
    // ENTITY → DTO
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "subcategoryId", source = "subcategory.id")
    MachineDTO toDto(Machine s);

    // DTO → ENTITY
    @Mapping(target = "category", source = "categoryId")
    @Mapping(target = "subcategory", source = "subcategoryId", qualifiedByName = "subcategoryFromId")
    Machine toEntity(MachineDTO dto); //MapStruct generates an implementation class for these interface.

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    /**
     * Maps a {@link Long} ID to a {@link Category} entity.
     *
     * @param id the ID of the subcategory.
     * @return a {@link Category} entity with the given ID, or null if the ID is null.
     */
    default Category map(Long id) {
        if (id == null) {
            return null;
        }
        Category category = new Category();
        category.setId(id);
        return category;
    }

    /**
     * Maps a {@link Long} ID to a {@link Subcategory} entity.
     *
     * @param id the ID of the subcategory.
     * @return a {@link Subcategory} entity with the given ID, or null if the ID is null.
     */
    @Named("subcategoryFromId")
    default Subcategory mapSubcategory(Long id) {
        if (id == null) {
            return null;
        }
        Subcategory subcategory = new Subcategory();
        subcategory.setId(id);
        return subcategory;
    }
}

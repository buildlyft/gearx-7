package com.gearx7.app.service.mapper;

import com.gearx7.app.domain.Machine;
import com.gearx7.app.domain.User;
import com.gearx7.app.service.dto.MachineDTO;
import com.gearx7.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Machine} and its DTO {@link MachineDTO}.
 */
@Mapper(componentModel = "spring")
public interface MachineMapper extends EntityMapper<MachineDTO, Machine> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    MachineDTO toDto(Machine s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}

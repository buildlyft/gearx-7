package com.gearx7.app.service.mapper;

import com.gearx7.app.domain.Category;
import com.gearx7.app.service.dto.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(source = "type.id", target = "typeId")
    CategoryDTO toDto(Category entity);

    @Mapping(source = "typeId", target = "type.id")
    Category toEntity(CategoryDTO dto);
}

package com.gearx7.app.service.mapper;

import com.gearx7.app.domain.Subcategory;
import com.gearx7.app.service.dto.SubCategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubcategoryMapper {
    // Entity → DTO
    @Mapping(source = "category.id", target = "categoryId")
    SubCategoryDTO toDto(Subcategory entity);

    // DTO → Entity
    @Mapping(source = "categoryId", target = "category.id")
    Subcategory toEntity(SubCategoryDTO dto);
}

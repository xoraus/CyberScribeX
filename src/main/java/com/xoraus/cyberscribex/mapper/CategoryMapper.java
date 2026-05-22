package com.xoraus.cyberscribex.mapper;

import com.xoraus.cyberscribex.entity.Category;
import com.xoraus.cyberscribex.payload.CategoryDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDto);

    List<CategoryDto> toDtoList(List<Category> categories);
}

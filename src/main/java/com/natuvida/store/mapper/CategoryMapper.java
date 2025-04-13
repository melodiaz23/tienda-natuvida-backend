package com.natuvida.store.mapper;

import com.natuvida.store.dto.response.CategoryResponseDTO;
import com.natuvida.store.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CategoryMapper {


  CategoryResponseDTO toDto(Category entity);

  Category toEntity(CategoryResponseDTO dto);

  List<CategoryResponseDTO> toDtoList(List<Category> entityList);

  List<Category> toEntityList(List<CategoryResponseDTO> dtoList);


}

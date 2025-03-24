package com.natuvida.store.mapper;

import com.natuvida.store.dto.CategoryDTO;
import com.natuvida.store.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CategoryMapper {


  CategoryDTO toDto(Category entity);

  Category toEntity(CategoryDTO dto);

  List<CategoryDTO> toDtoList(List<Category> entityList);

  List<Category> toEntityList(List<CategoryDTO> dtoList);


}

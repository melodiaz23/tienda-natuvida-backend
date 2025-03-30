package com.natuvida.store.mapper;

import com.natuvida.store.dto.response.ProductDTO;
import com.natuvida.store.dto.request.ProductRequestDTO;
import com.natuvida.store.entity.Product;
import com.natuvida.store.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ProductImageMapper.class, ProductPricingMapper.class})
public interface ProductMapper {

  @Mapping(target = "categoryIds", source = "categories", qualifiedByName = "categoriesToIds")
  @Mapping(target = "categoryNames", source = "categories", qualifiedByName = "categoriesToNames")
  ProductDTO toDto(Product entity);

  @Mapping(target = "categories", source = "categoryIds", qualifiedByName = "idsToCategories")
  @Mapping(target = "pricing", source = "pricing")
  Product toEntity(ProductDTO dto);

  List<Product> toEntityList(List<ProductDTO> dtoList);

  List<ProductDTO> toDtoList(List<Product> entityList);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "categories", source = "categoryIds", qualifiedByName = "idsToCategories")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Product toEntity(ProductRequestDTO requestDto);

  @Named("categoriesToIds")
  default List<UUID> categoriesToIds(List<Category> categories) {
    if (categories == null) {
      return new ArrayList<>();
    }
    return categories.stream()
        .map(Category::getId)
        .collect(Collectors.toList());
  }

  @Named("categoriesToNames")
  default List<String> categoriesToNames(List<Category> categories) {
    if (categories == null) {
      return new ArrayList<>();
    }
    return categories.stream()
        .map(Category::getName)
        .collect(Collectors.toList());
  }

  @Named("idsToCategories")
  default List<Category> idsToCategories(List<UUID> ids) {
    if (ids == null) {
      return new ArrayList<>();
    }
    return ids.stream()
        .map(id -> {
          Category category = new Category();
          category.setId(id);
          return category;
        })
        .collect(Collectors.toList());
  }
}
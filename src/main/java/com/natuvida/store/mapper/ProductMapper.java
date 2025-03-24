package com.natuvida.store.mapper;

import com.natuvida.store.dto.ProductDTO;
import com.natuvida.store.dto.ProductRequestDTO;
import com.natuvida.store.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductImageMapper.class, ProductPricingMapper.class})
public interface ProductMapper {

  @Mapping(target = "categoryName", source = "category.name")
  ProductDTO toDto(Product entity);

  @Mapping(target = "category.id", source = "categoryId")
  @Mapping(target = "pricing", source = "pricing")
  Product toEntity(ProductDTO dto);

  List<Product> toEntityList(List<ProductDTO> dtoList);

  List<ProductDTO> toDtoList(List<Product> entityList);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "category.id", source = "categoryId")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Product toEntity(ProductRequestDTO requestDto);
}


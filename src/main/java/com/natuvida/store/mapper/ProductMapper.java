package com.natuvida.store.mapper;

import com.natuvida.store.dto.response.ProductResponseDTO;
import com.natuvida.store.dto.request.ProductRequestDTO;
import com.natuvida.store.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductImageMapper.class, PriceMapper.class})
public interface ProductMapper {

  ProductResponseDTO toDto(Product entity);

  Product toEntity(ProductResponseDTO dto);

  List<Product> toEntityList(List<ProductResponseDTO> dtoList);

  List<ProductResponseDTO> toDtoList(List<Product> entityList);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "categories", ignore = true)
  Product toEntity(ProductRequestDTO requestDto);


}
package com.natuvida.store.mapper;

import com.natuvida.store.dto.response.ProductImageResponseDTO;
import com.natuvida.store.entity.ProductImage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

  ProductImage toEntity(ProductImageResponseDTO dto);
  List<ProductImage> toEntityList(List<ProductImageResponseDTO> dtoList);
  ProductImageResponseDTO toDto(ProductImage entity);
  List<ProductImageResponseDTO> toDtoList(List<ProductImage> entityList);
}
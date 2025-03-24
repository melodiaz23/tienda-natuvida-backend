package com.natuvida.store.mapper;

import com.natuvida.store.dto.ProductImageDTO;
import com.natuvida.store.entity.ProductImage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

  ProductImage toEntity(ProductImageDTO dto);
  List<ProductImage> toEntityList(List<ProductImageDTO> dtoList);
  ProductImageDTO toDto(ProductImage entity);
  List<ProductImageDTO> toDtoList(List<ProductImage> entityList);
}
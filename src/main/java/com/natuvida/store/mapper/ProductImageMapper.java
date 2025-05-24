package com.natuvida.store.mapper;

import com.natuvida.store.dto.request.ProductImageRequestDTO;
import com.natuvida.store.dto.response.ProductImageResponseDTO;
import com.natuvida.store.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

  @Mapping(target = "version", constant = "0")
  ProductImage toEntity(ProductImageRequestDTO dto);

  ProductImageResponseDTO toDto(ProductImage entity);

  List<ProductImageResponseDTO> toDtoList(List<ProductImage> entityList);
}
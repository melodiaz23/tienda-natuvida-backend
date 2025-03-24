package com.natuvida.store.mapper;

import com.natuvida.store.dto.ProductPricingDTO;
import com.natuvida.store.entity.ProductPricing;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductPricingMapper {
  ProductPricing toEntity(ProductPricingDTO dto);

  List<ProductPricing> toEntityList(List<ProductPricingDTO> dtoList);

  ProductPricingDTO toDto(ProductPricing entity);

  List<ProductPricingDTO> toDtoList(List<ProductPricing> entityList);
}
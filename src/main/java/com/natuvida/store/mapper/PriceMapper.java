package com.natuvida.store.mapper;

import com.natuvida.store.dto.response.PriceResponseDTO;
import com.natuvida.store.entity.Price;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceMapper {

  Price toEntity(PriceResponseDTO dto);
  List<Price> toEntityList(List<PriceResponseDTO> dtoList);

  PriceResponseDTO toDto(Price entity);

  List<PriceResponseDTO> toDtoList(List<Price> entityList);
}
package com.natuvida.store.mapper;

import com.natuvida.store.dto.response.PriceDTO;
import com.natuvida.store.entity.Price;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceMapper {

  Price toEntity(PriceDTO dto);
  List<Price> toEntityList(List<PriceDTO> dtoList);

  PriceDTO toDto(Price entity);

  List<PriceDTO> toDtoList(List<Price> entityList);
}
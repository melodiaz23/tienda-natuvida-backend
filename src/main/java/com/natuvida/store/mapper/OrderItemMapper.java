package com.natuvida.store.mapper;

import com.natuvida.store.dto.response.OrderItemResponseDTO;
import com.natuvida.store.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

  @Mapping(target = "productId", source = "product.id")
  OrderItemResponseDTO toDto(OrderItem item);

  List<OrderItemResponseDTO> toDtoList(List<OrderItem> items);
}
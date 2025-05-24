package com.natuvida.store.mapper;

import com.natuvida.store.dto.response.OrderResponseDTO;
import com.natuvida.store.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, CustomerMapper.class})
public interface OrderMapper {

  @Mapping(target = "customerId", source = "customer.id")
  @Mapping(target = "items", source = "orderItems")
  OrderResponseDTO toDto(Order entity);

  List<OrderResponseDTO> toDtoList(List<Order> orders);
}
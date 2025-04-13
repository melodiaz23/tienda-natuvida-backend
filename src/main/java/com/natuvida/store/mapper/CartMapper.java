package com.natuvida.store.mapper;

import com.natuvida.store.dto.request.CartRequestDTO;
import com.natuvida.store.dto.response.CartResponseDTO;
import com.natuvida.store.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

// `uses`, especifica que se va a usar el mapper CartItemMapper y UserMapper para mapear los objetos
@Mapper(componentModel = "spring", uses = {CartItemMapper.class, UserMapper.class})
public interface CartMapper {

  @Mapping(target = "userId", source = "user.id")
  CartResponseDTO toDto(Cart entity);

  // Target se refiere al objeto de salida que se va a crear
  @Mapping(target = "user.id", source = "userId")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "totalPrice", ignore = true)
  Cart toEntity(CartRequestDTO dto);

  List<CartResponseDTO> toDtoList(List<Cart> carts);
}
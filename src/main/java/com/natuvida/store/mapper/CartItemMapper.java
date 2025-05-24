package com.natuvida.store.mapper;

import com.natuvida.store.dto.response.CartItemResponseDTO;
import com.natuvida.store.entity.CartItem;
import com.natuvida.store.entity.Product;
import com.natuvida.store.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PriceMapper.class})
public interface CartItemMapper {

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "productName", source = "product.name")
  @Mapping(target = "price", source = "product.price")
  // Expresion Java para obtener la URL de la imagen del producto
  @Mapping(target = "productImageUrl", expression = "java(getProductImageUrl(entity.getProduct()))")
  CartItemResponseDTO toDto(CartItem entity);

  List<CartItemResponseDTO> toDtoList(List<CartItem> cartItems);

  default String getProductImageUrl(Product product) {
    if (product != null && product.getImages() != null && !product.getImages().isEmpty()) {
      return product.getImages().stream()
          .filter(ProductImage::isPrimary)
          .findFirst()
          .map(ProductImage::getImageUrl)
          .orElse(product.getImages().get(0).getImageUrl());
    }
    return null;
  }
}
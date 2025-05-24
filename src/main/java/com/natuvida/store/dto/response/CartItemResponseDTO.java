package com.natuvida.store.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CartItemResponseDTO {
  private UUID id;
  private UUID productId;
  private String productName;
  private String productImageUrl;
  private Integer quantity;
  private BigDecimal unitPrice;
  private PriceResponseDTO price;
  private BigDecimal subtotal;
}
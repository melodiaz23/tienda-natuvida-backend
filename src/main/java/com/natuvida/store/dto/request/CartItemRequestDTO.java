package com.natuvida.store.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CartItemRequestDTO {
  private UUID productId;
  private Integer quantity;
}
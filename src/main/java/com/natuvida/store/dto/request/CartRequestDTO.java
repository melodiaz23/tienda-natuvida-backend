package com.natuvida.store.dto.request;

import com.natuvida.store.enums.CartStatus;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CartRequestDTO {
  private UUID userId;
  private List<CartItemRequestDTO> items;
  private CartStatus status;
}
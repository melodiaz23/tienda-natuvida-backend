package com.natuvida.store.dto.response;

import com.natuvida.store.enums.CartStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CartResponseDTO {
  private UUID id;
  private UUID userId;
  private List<CartItemResponseDTO> items;
  private BigDecimal totalPrice;
  private CartStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
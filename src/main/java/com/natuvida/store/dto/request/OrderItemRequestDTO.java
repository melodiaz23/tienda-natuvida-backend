package com.natuvida.store.dto.request;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Getter
@Setter
public class OrderItemRequestDTO {
  @NotNull(message = "El ID del producto es requerido")
  private UUID productId;

  @NotNull(message = "La cantidad es requerida")
  @Min(value = 1, message = "La cantidad mínima debe ser 1")
  private Integer quantity;


}
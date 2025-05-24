package com.natuvida.store.dto.request;

import com.natuvida.store.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderRequestDTO {
  @NotNull(message = "El ID del cliente es requerido")
  private UUID customerId;

  @NotBlank(message = "La dirección de envío es requerida")
  private String shippingAddress;

  @NotNull(message = "El método de pago es requerido")
  private PaymentMethod paymentMethod;

  @NotEmpty(message = "Se requiere al menos un ítem en la orden")
  private List<OrderItemRequestDTO> items;

  private String notes;
}
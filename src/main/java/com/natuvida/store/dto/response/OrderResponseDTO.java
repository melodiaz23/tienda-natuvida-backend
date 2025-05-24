package com.natuvida.store.dto.response;

import com.natuvida.store.enums.OrderStatus;
import com.natuvida.store.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderResponseDTO {
  private UUID id;
  private String orderNumber;
  private LocalDateTime orderDate;
  private UUID customerId;
  private CustomerResponseDTO customer;
  private OrderStatus status;
  private BigDecimal totalAmount;
  private List<OrderItemResponseDTO> items;
  private String shippingAddress;
  private PaymentMethod paymentMethod;
  private String trackingNumber;
  private String notes;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
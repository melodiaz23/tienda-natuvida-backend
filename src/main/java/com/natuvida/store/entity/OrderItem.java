package com.natuvida.store.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "order_items")
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;

  @ManyToOne
  @NonNull
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @NonNull
  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal unitPrice;

  @Column(precision = 12, scale = 2)
  private BigDecimal subtotal;

  @Column
  private String productName; // Store the name at time of order

  @Column(precision = 12, scale = 2)
  private BigDecimal discount;


}

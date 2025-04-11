package com.natuvida.store.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "cart_items")
@NoArgsConstructor
public class CartItem {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_id", nullable = false)
  private Cart cart;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Min(1)
  @Column(nullable = false)
  private Integer quantity;

  @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
  private BigDecimal unitPrice;

  @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
  private BigDecimal subtotal;
}
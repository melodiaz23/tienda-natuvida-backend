package com.natuvida.store.entity;

import jakarta.persistence.*;
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

  @Column(nullable = false)
  private Integer quantity;

  @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
  private BigDecimal unitPrice;

  @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
  private BigDecimal subtotal;

  public CartItem(Product product, Integer quantity) {
    this.product = product;
    this.quantity = quantity;
    this.unitPrice = product.getPrice();
    this.recalculateSubtotal();
  }

  // Helper methods
  public void incrementQuantity() {
    this.quantity++;
    recalculateSubtotal();
  }

  public void decrementQuantity() {
    if (this.quantity > 1) {
      this.quantity--;
      recalculateSubtotal();
    }
  }

  public void updateQuantity(Integer quantity) {
    if (quantity > 0) {
      this.quantity = quantity;
      recalculateSubtotal();
    }
  }

  public void recalculateSubtotal() {
    this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    if (this.cart != null) {
      this.cart.recalculateTotal();
    }
  }



}

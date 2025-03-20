package com.natuvida.store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    this.unitPrice = getPrice();
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
    // Get the total price based on quantity
    ProductPricing pricing = this.product.getPricing();
    if (pricing == null) {
      this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    } else {
      // Apply tiered pricing
      if (this.quantity == 1) {
        this.subtotal = pricing.getUnitPrice();
      } else if (this.quantity == 2) {
        this.subtotal = pricing.getPriceTwoUnits();
      } else if (this.quantity == 3) {
        this.subtotal = pricing.getPriceThreeUnits();
      } else {
        // For quantities > 3, calculate based on multiples of 3
        int sets = this.quantity / 3;
        int remainder = this.quantity % 3;
        this.subtotal = pricing.getPriceThreeUnits().multiply(BigDecimal.valueOf(sets));
        if (remainder == 1) {
          this.subtotal = this.subtotal.add(pricing.getUnitPrice());
        } else if (remainder == 2) {
          this.subtotal = this.subtotal.add(pricing.getPriceTwoUnits());
        }
      }
    }

    if (this.cart != null) {
      this.cart.recalculateTotal();
    }
  }

  private BigDecimal getPrice() {
    ProductPricing pricing = this.product.getPricing();

    if (pricing == null) {
      throw new RuntimeException("Pricing not found for product: " + this.product.getId());
    }

    if (this.quantity == 1) {
      return pricing.getUnitPrice();
    } else if (this.quantity == 2) {
      return pricing.getPriceTwoUnits().divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    } else if (this.quantity >= 3) {
      return pricing.getPriceThreeUnits().divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
    }
    return BigDecimal.ZERO;
  }



}

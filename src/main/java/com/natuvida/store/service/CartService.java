package com.natuvida.store.service;

import com.natuvida.store.entity.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartService {
//  // Other methods...
//
//  /**
//   * Calculates the subtotal for a cart item based on quantity and tiered pricing
//   *
//   * @param product  The product
//   * @param quantity The quantity ordered
//   * @return The calculated subtotal
//   */
//  public BigDecimal calculateItemSubtotal(Product product, int quantity) {
//    // Get the pricing info from the product
//    ProductPricing pricing = product.getPricing();
//    BigDecimal subtotal;
//
//    if (pricing == null) {
//       Simple calculation based on unit price
//      subtotal = product.getUnitPrice().multiply(BigDecimal.valueOf(quantity));
//    } else {
//      // Apply tiered pricing logic
//      if (quantity == 1) {
//        subtotal = pricing.getUnitPrice();
//      } else if (quantity == 2) {
//        subtotal = pricing.getPriceTwoUnits();
//      } else if (quantity == 3) {
//        subtotal = pricing.getPriceThreeUnits();
//      } else {
//        // For quantities > 3, calculate based on multiples of 3
//        int sets = quantity / 3;
//        int remainder = quantity % 3;
//        subtotal = pricing.getPriceThreeUnits().multiply(BigDecimal.valueOf(sets));
//
//        if (remainder == 1) {
//          subtotal = subtotal.add(pricing.getUnitPrice());
//        } else if (remainder == 2) {
//          subtotal = subtotal.add(pricing.getPriceTwoUnits());
//        }
//      }
//    }
//
//    return subtotal;
//  }
}
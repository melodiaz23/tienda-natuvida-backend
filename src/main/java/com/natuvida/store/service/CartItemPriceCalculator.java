package com.natuvida.store.service;

import com.natuvida.store.entity.Price;
import com.natuvida.store.entity.Product;
import com.natuvida.store.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartItemPriceCalculator {
  public BigDecimal calculateSubtotal(Product product, int quantity) {
    Price pricing = product.getPrice();
    if (pricing == null) {
      throw new ValidationException("Product price not found");
    }

    BigDecimal subtotal;
    switch (quantity) {
      case 1 -> subtotal = pricing.getUnit();
      case 2 -> subtotal = pricing.getTwoUnits() != null ?
          pricing.getTwoUnits() : pricing.getUnit().multiply(BigDecimal.valueOf(2));
      case 3 -> subtotal = pricing.getThreeUnits() != null ?
          pricing.getThreeUnits() : pricing.getUnit().multiply(BigDecimal.valueOf(3));
      default -> {
        // For quantities > 3, calculate based on promotions if available
        int sets = quantity / 3;
        int remainder = quantity % 3;
        subtotal = calculateMultiSetPrice(pricing, sets, remainder);
      }
    }
    return subtotal;
  }

  private BigDecimal calculateMultiSetPrice(Price pricing, int sets, int remainder) {
    BigDecimal basePrice = pricing.getThreeUnits() != null ?
        pricing.getThreeUnits().multiply(BigDecimal.valueOf(sets)) :
        pricing.getUnit().multiply(BigDecimal.valueOf(sets * 3));

    if (remainder == 0) return basePrice;
    if (remainder == 1) return basePrice.add(pricing.getUnit());
    return basePrice.add(pricing.getTwoUnits() != null ?
        pricing.getTwoUnits() : pricing.getUnit().multiply(BigDecimal.valueOf(2)));
  }
}
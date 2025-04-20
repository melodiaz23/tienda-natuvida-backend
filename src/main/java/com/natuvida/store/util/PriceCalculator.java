package com.natuvida.store.util;

import com.natuvida.store.entity.Price;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class PriceCalculator {

  public PriceResult calculateItemPrices(Price price, int quantity) {
    BigDecimal subtotal;

    if (quantity == 1) {
      subtotal = price.getUnit();
    } else if (quantity == 2) {
      subtotal = price.getTwoUnits() != null ? price.getTwoUnits() : price.getUnit().multiply(BigDecimal.valueOf(2));
    } else if (quantity == 3) {
      subtotal = price.getThreeUnits() != null ? price.getThreeUnits() : price.getUnit().multiply(BigDecimal.valueOf(3));
    } else if (quantity > 3) {
      subtotal = calculateComplexPrice(price, quantity);
    } else {
      subtotal = price.getUnit().multiply(BigDecimal.valueOf(quantity));
    }

    BigDecimal unitPrice = subtotal.divide(BigDecimal.valueOf(quantity), 2, BigDecimal.ROUND_HALF_UP);

    return new PriceResult(subtotal, unitPrice);
  }

  /**
   * Calcula precios para cantidades más complejas considerando promociones
   */
  private BigDecimal calculateComplexPrice(Price price, int quantity) {
    int promotionSet = quantity / 5;
    int remainder = quantity % 5;

    // Precio por 5 unidades (con o sin promoción)
    BigDecimal setPrice = price.getUnit().multiply(BigDecimal.valueOf(3));

    // Subtotal para los conjuntos completos
    BigDecimal subtotal = setPrice.multiply(BigDecimal.valueOf(promotionSet));

    // Si no hay unidades restantes, devolver el subtotal de los conjuntos
    if (remainder == 0) {
      return subtotal;
    }

    // Calcular precio para las unidades restantes
    BigDecimal remainderSubtotal;

    if (remainder == 1) {
      remainderSubtotal = price.getUnit();
    } else if (remainder == 2) {
      remainderSubtotal = price.getTwoUnits() != null
          ? price.getTwoUnits()
          : price.getUnit().multiply(BigDecimal.valueOf(2));
    } else if (remainder == 3) {
      remainderSubtotal = price.getThreeUnits() != null
          ? price.getThreeUnits()
          : price.getUnit().multiply(BigDecimal.valueOf(3));
    } else { // remainder == 4
      if (price.getThreeUnits() != null) {
        // Si tenemos precio para 3, calculamos 4 unidades como (precio por unidad de 3) * 4/3
        BigDecimal pricePerUnit = price.getThreeUnits().divide(BigDecimal.valueOf(3), 2, BigDecimal.ROUND_HALF_UP);
        remainderSubtotal = pricePerUnit.multiply(BigDecimal.valueOf(4));
      } else {
        remainderSubtotal = price.getUnit().multiply(BigDecimal.valueOf(4));
      }
    }

    return subtotal.add(remainderSubtotal);
  }

  /**
   * Clase interna para devolver los resultados del cálculo
   */
  public static class PriceResult {
    private final BigDecimal subtotal;
    private final BigDecimal unitPrice;

    public PriceResult(BigDecimal subtotal, BigDecimal unitPrice) {
      this.subtotal = subtotal;
      this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
      return subtotal;
    }

    public BigDecimal getUnitPrice() {
      return unitPrice;
    }
  }
}
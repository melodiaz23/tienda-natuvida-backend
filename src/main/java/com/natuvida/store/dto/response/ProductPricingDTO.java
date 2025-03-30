package com.natuvida.store.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductPricingDTO {
  private UUID id;
  private BigDecimal unitPrice;
  private BigDecimal priceTwoUnits;
  private BigDecimal priceThreeUnits;
  private BigDecimal previousPrice;
}
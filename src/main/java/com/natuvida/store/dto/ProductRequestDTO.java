package com.natuvida.store.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductRequestDTO {
  private String name;
  private String description;
  private BigDecimal unitPrice;
  private BigDecimal priceTwoUnits;
  private BigDecimal priceThreeUnits;
  private BigDecimal previousPrice;
  private UUID categoryId;

}

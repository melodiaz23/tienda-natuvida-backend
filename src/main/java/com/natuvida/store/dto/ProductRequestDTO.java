package com.natuvida.store.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ProductRequestDTO {
  private String name;
  private String description;
  private String preparation;
  private String ingredients;
  private ProductPricingDTO pricing;
  private UUID categoryId;
  private List<ProductImageDTO> images;

}

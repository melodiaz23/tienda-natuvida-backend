package com.natuvida.store.dto.request;

import com.natuvida.store.dto.ProductImageDTO;
import com.natuvida.store.dto.response.ProductPricingDTO;
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
  private List<UUID> categoryIds;
  private List<ProductImageDTO> images;
}
package com.natuvida.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
  private UUID id;
  private String name;
  private String description;
  private String preparation;
  private String ingredients;
  private ProductPricingDTO pricing;
  private UUID categoryId;
  private String categoryName;  // Added for frontend display
  private List<ProductImageDTO> images;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public String getPrimaryImageUrl() {
    if (images == null || images.isEmpty()) {
      return null;
    }

    return images.stream()
        .filter(ProductImageDTO::isPrimary)
        .findFirst()
        .map(ProductImageDTO::getImageUrl)
        .orElse(images.get(0).getImageUrl());  // Fallback to first image
  }
}
package com.natuvida.store.dto.response;

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
  private String presentation;
  private List<String> ingredients;
  private List<String> benefits;
  private List<String> tags;
  private String usageMode;
  private PriceDTO price;
  private List<CategoryDTO> categories;
  private List<ProductImageDTO> images;
  private boolean enabled;
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
        .orElse(images.get(0).getImageUrl());
  }
}
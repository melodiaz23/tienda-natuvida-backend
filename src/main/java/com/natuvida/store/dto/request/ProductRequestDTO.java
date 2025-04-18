package com.natuvida.store.dto.request;

import com.natuvida.store.dto.response.CategoryResponseDTO;
import com.natuvida.store.entity.Price;
import com.natuvida.store.entity.ProductImage;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ProductRequestDTO {
  private UUID id;
  private String name;
  private String customName;
  private String description;
  private String presentation;
  private List<String> ingredients;
  private List<String> benefits;
  private List<String> tags;
  private String usageMode;
  private Price price;
  private List<CategoryResponseDTO> categories;
  private List<ProductImage> images;
  private boolean enabled;
}
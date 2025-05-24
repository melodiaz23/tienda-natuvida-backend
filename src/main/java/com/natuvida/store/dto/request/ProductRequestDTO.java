package com.natuvida.store.dto.request;

import com.natuvida.store.entity.Price;
import lombok.Getter;
import java.util.List;
import java.util.UUID;

@Getter
public class ProductRequestDTO {
  private UUID id;
  private String name;
  private String customName;
  private String description;
  private String presentation;
  private List<String> ingredients;
  private List<String> benefits;
  private List<String> tags;
  private List<String> bonuses;
  private List<String> contraindications;
  private String usageMode;
  private Price price;
  private List<UUID> categories;
  private List<ProductImageRequestDTO> images;
  private boolean enabled;


}
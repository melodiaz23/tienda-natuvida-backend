package com.natuvida.store.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageDTO {
  private String imageUrl;
  private String altText;
  private Integer displayOrder;
  private boolean isPrimary;
}
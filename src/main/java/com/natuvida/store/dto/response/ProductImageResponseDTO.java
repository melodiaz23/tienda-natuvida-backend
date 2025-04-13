package com.natuvida.store.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class ProductImageResponseDTO {
  private UUID id;
  private String imageUrl;
  private String altText;
  private Integer displayOrder;
  private boolean isPrimary;
}
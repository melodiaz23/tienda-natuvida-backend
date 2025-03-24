package com.natuvida.store.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CategoryDTO {

  private UUID id;
  private String name;
  private String description;
  private List<ProductDTO> products;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;



}

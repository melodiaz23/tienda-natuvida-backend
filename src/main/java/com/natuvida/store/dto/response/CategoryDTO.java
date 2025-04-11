package com.natuvida.store.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
  private UUID id;
  private String name;
  private String description;
  private List<UUID> productIds;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}
package com.natuvida.store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProductImage {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String imageUrl;
  private String altText;
  private Integer displayOrder;
  private boolean isPrimary = false;

}

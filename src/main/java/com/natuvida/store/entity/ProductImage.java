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

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  private String imageUrl;    // Store the cloud storage URL here
  private String altText;     // For accessibility
  private Integer displayOrder;
  private boolean isPrimary = false;

}

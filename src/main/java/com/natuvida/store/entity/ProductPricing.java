package com.natuvida.store.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "product_pricing")
public class ProductPricing {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @OneToOne(mappedBy = "pricing")
  private Product product;

  @Column(nullable = false)
  private BigDecimal unitPrice;

  private BigDecimal priceTwoUnits;
  private BigDecimal priceThreeUnits;
  private BigDecimal previousPrice;
}
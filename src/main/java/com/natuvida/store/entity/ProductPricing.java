package com.natuvida.store.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProductPricing {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonBackReference
  @OneToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(nullable = false)
  private BigDecimal unitPrice;

  private BigDecimal priceTwoUnits;
  private BigDecimal priceThreeUnits;
  private BigDecimal previousPrice;
}
package com.natuvida.store.entity;

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
@Table(name = "price")
public class Price {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @OneToOne(mappedBy = "price")
  private Product product;

  @Column(nullable = false)
  private BigDecimal unit;
  private BigDecimal twoUnits;
  private BigDecimal threeUnits;
}
package com.natuvida.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "products")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NonNull
  @Column(nullable = false, length = 50)
  private String name;

  @Column(length = 2000)
  private String description;

  private String presentation;

  @ElementCollection
  @CollectionTable(name = "product_ingredients", joinColumns = @JoinColumn(name = "product_id"))
  @Column(name = "ingredient")
  private List<String> ingredients = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "product_benefits", joinColumns = @JoinColumn(name = "product_id"))
  @Column(name = "benefit", length = 500)
  private List<String> benefits = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
  @Column(name = "tag")
  private List<String> tags = new ArrayList<>();

  @Column(length = 500)
  private String usageMode;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "price_id")
  private Price price;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "product_categories",  // Nombre de la tabla intermedia
      joinColumns = @JoinColumn(name = "product_id"),  // Columna que referencia a esta entidad
      inverseJoinColumns = @JoinColumn(name = "category_id")  // Columna que referencia a la otra entidad
  )
  private List<Category> categories = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id")
  private List<ProductImage> images = new ArrayList<>();

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "product")
  private List<OrderItem> orderItems;

  @Column(nullable = false)
  private boolean enabled = true;

}

package com.natuvida.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
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

  @Column(length = 500)
  private String description;

  @Column(length = 500)
  private String preparation;

  @Column(length = 250)
  private String ingredients;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "pricing_id")
  private ProductPricing pricing;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

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

  public void addImage(ProductImage image) {
    images.add(image);
  }

  public void removeImage(ProductImage image) {
    images.remove(image);
  }

//  The field exists only in the Java object,
  @Transient // tell JPA don't map this getter to a database column
  public String getPrimaryImageUrl() {
    return images.stream()
        .filter(ProductImage::isPrimary)
        .findFirst()
        .map(ProductImage::getImageUrl)
        .orElse(null);
  }

}

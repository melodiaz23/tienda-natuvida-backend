package com.natuvida.store.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

  @Column(length = 500)
  private String description;

  @JsonManagedReference // Manage bidirectional relation with ProductPricing
  @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private ProductPricing pricing;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
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
    image.setProduct(this);
  }

  public void removeImage(ProductImage image) {
    images.remove(image);
    image.setProduct(null);
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

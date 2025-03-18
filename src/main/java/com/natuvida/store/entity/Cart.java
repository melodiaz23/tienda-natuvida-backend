package com.natuvida.store.entity;

import com.natuvida.store.enums.CartStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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
@EntityListeners(AuditingEntityListener.class)
@Table(name="carts")
public class Cart {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "created_at")
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "session_id")
  private String sessionId; // For guest users

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CartItem> items = new ArrayList<>();

  @Column(name = "total_price", precision = 10, scale = 2)
  private BigDecimal totalPrice = BigDecimal.ZERO;

  @Enumerated(EnumType.STRING)
  private CartStatus status = CartStatus.ACTIVE;

  public void addItem(CartItem item) {
    items.add(item);
    item.setCart(this);
    recalculateTotal();
  }

  public void removeItem(CartItem item) {
    items.remove(item);
    item.setCart(null);
    recalculateTotal();
  }

  public void recalculateTotal(){
    this.totalPrice = items.stream()
        .map(CartItem::getSubtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public CartItem findItemByProductId(UUID productId) {
    if (productId == null) {
      return null;
    }
    return items.stream()
        .filter(i -> productId.equals(i.getProduct().getId())).findFirst().orElse(null);

  }

  public void clearItems() {
    items.forEach(item -> item.setCart(null));
    items.clear();
    totalPrice = BigDecimal.ZERO;
  }



}

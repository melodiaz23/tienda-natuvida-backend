package com.natuvida.store.entity;

import com.natuvida.store.enums.OrderStatus;
import com.natuvida.store.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NonNull
  @Column(nullable = false, unique = true)
  private String orderNumber;

  @NonNull
  @Column(nullable = false)
  private LocalDateTime orderDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  @Enumerated(EnumType.STRING)
  private OrderStatus status = OrderStatus.PENDING; // Default status

  @Column(precision = 10, scale = 2)
  private BigDecimal totalAmount;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> orderItems = new ArrayList<>();

  @NonNull
  @Column(nullable = false)
  private String shippingAddress;

  @Enumerated(EnumType.STRING)
  @Column
  private PaymentMethod paymentMethod;

  @Column
  private String trackingNumber;

  @Column
  private String notes;

  // Helper methods.
  public void addOrderItem(OrderItem item){
    orderItems.add(item);
    item.setOrder(this);
    // Recalculate total
    this.calculateTotal();
  }

  public void removeOrderItem(OrderItem item) {
    orderItems.remove(item);
    item.setOrder(null);
    // Recalculate total
    this.calculateTotal();
  }

  private void calculateTotal() {
    this.totalAmount = orderItems.stream()
        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

}

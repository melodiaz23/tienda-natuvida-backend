package com.natuvida.store.repository;

import com.natuvida.store.entity.Order;
import com.natuvida.store.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
  List<Order> findByCustomerId(UUID customerId);

  Optional<Order> findByOrderNumber(String orderNumber);

  List<Order> findByStatus(OrderStatus status);

  @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(order_number, 4) AS SIGNED)), 20000) FROM orders", nativeQuery = true)
  Integer getMaxOrderNumber();
}

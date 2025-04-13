package com.natuvida.store.repository;

import com.natuvida.store.entity.Cart;
import com.natuvida.store.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
  // To find a cart by userId, status and enabled
  Optional<Cart> findByUserIdAndStatusAndEnabled(UUID userId, CartStatus status, Boolean enabled);

}

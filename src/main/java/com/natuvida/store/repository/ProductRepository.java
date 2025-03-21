package com.natuvida.store.repository;

import com.natuvida.store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

  // Find products by category
  List<Product> findByCategoryId(UUID categoryId);

  // Find products by price range
  List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}

package com.natuvida.store.repository;

import com.natuvida.store.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
  // Find primary/main image for a product
  Optional<ProductImage> findByProductIdAndIsPrimaryTrue(UUID productId);

}

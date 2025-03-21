package com.natuvida.store.repository;

import com.natuvida.store.entity.ProductPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProductPricingRepository extends JpaRepository<ProductPricing, UUID> {
}

package com.natuvida.store.repository;

import com.natuvida.store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByCategoriesId(UUID categoryId);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    Optional<Product> findBySlug(String slug);

}

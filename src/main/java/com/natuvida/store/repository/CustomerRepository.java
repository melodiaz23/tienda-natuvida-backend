package com.natuvida.store.repository;

import com.natuvida.store.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
  Optional<Customer> findByUserId(UUID userId);
  Optional<Customer> findByUserEmail(String email);
  Optional<Customer> findByNationalId(String nationalId);
  boolean existsByNationalId(String nationalId);
}



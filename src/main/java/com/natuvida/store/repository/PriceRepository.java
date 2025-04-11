package com.natuvida.store.repository;

import com.natuvida.store.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PriceRepository extends JpaRepository<Price, UUID> {

}

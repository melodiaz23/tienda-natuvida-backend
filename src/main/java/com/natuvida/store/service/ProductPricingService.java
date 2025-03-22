package com.natuvida.store.service;

import com.natuvida.store.entity.ProductPricing;
import com.natuvida.store.exception.ValidationException;
import com.natuvida.store.repository.ProductPricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProductPricingService {
  @Autowired
  ProductPricingRepository productPricingRepository;

  @Transactional
  public ProductPricing setOrUpdatePrices(UUID idPricing, BigDecimal unitPrice,
                                  BigDecimal priceTwoUnits,
                                  BigDecimal priceThreeUnits,
                                  BigDecimal previousPrice){
    if (unitPrice==null) throw new ValidationException("Precio unitario es obligatorio");
    ProductPricing prices;
    if (idPricing == null){
      prices = new ProductPricing();
    } else {
      prices = productPricingRepository.getReferenceById(idPricing);
    }
    prices.setUnitPrice(unitPrice);
    prices.setPriceTwoUnits(Objects.requireNonNullElseGet(priceTwoUnits, () -> unitPrice.multiply(BigDecimal.valueOf(2))));
    prices.setPriceThreeUnits(Objects.requireNonNullElseGet(priceThreeUnits, () -> unitPrice.multiply(BigDecimal.valueOf(3))));
    prices.setPreviousPrice(Objects.requireNonNullElseGet(previousPrice, null));
    return productPricingRepository.save(prices);
  }


}

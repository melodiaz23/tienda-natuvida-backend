package com.natuvida.store.service;

import com.natuvida.store.entity.ProductPricing;
import com.natuvida.store.exception.ValidationException;
import com.natuvida.store.repository.ProductPricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class ProductPricingService {
  @Autowired
  ProductPricingRepository productPricingRepository;

  @Transactional
  public ProductPricing setOrUpdatePrices(ProductPricing productPricing){
    if (productPricing.getUnitPrice()==null) throw new ValidationException("Precio unitario es obligatorio");
    ProductPricing prices;
    if (productPricing.getId() == null){
      prices = new ProductPricing();
    } else {
      prices = productPricingRepository.getReferenceById(productPricing.getId());
    }
    prices.setUnitPrice(productPricing.getUnitPrice());
    prices.setPriceTwoUnits(Objects.requireNonNullElseGet(productPricing.getPriceTwoUnits(), () -> productPricing.getUnitPrice().multiply(BigDecimal.valueOf(2))));
    prices.setPriceThreeUnits(Objects.requireNonNullElseGet(productPricing.getPriceThreeUnits(), () ->productPricing.getUnitPrice().multiply(BigDecimal.valueOf(3))));
    prices.setPreviousPrice(Objects.requireNonNullElseGet(productPricing.getPreviousPrice(), null));
    return productPricingRepository.save(prices);
  }


}

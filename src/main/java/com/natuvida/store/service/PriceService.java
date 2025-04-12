package com.natuvida.store.service;

import com.natuvida.store.entity.Price;
import com.natuvida.store.exception.ValidationException;
import com.natuvida.store.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class PriceService {
  @Autowired
  PriceRepository priceRepository;

  @Transactional
  public Price setOrUpdatePrices(Price price){
    if (price.getUnit()==null)
      throw new ValidationException("Precio unitario es obligatorio");
    Price prices;
    if (price.getId() == null){
      prices = new Price();
    } else {
      prices = priceRepository.getReferenceById(price.getId());
    }
    prices.setUnit(price.getUnit());
    prices.setTwoUnits(Objects.requireNonNullElseGet(price.getTwoUnits(),
        () -> price.getUnit().multiply(BigDecimal.valueOf(2))));
    prices.setThreeUnits(Objects.requireNonNullElseGet(price.getThreeUnits(),
        () ->price.getUnit().multiply(BigDecimal.valueOf(3))));
    prices.setThreeByTwo(price.getThreeByTwo());
    prices.setFiveByThree(price.getFiveByThree());
    return priceRepository.save(prices);
  }

  


}

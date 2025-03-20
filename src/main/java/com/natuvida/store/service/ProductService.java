package com.natuvida.store.service;

import com.natuvida.store.entity.Product;
import com.natuvida.store.entity.ProductPricing;
import com.natuvida.store.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {
  @Autowired
  ProductRepository productRepository;

  @Transactional(readOnly = true)
  public List<Product> getAllProducts(){
    return productRepository.findAll();
  }

  @Transactional
  public Product createProduct(){
    Product product = new Product("ColiPlus");
    product.setDescription("Suplemento natural para la salud intestinal");

    // Create pricing
    ProductPricing pricing = new ProductPricing();
    pricing.setUnitPrice(new BigDecimal("67900.00"));
    pricing.setPriceTwoUnits(new BigDecimal("101850.00"));
    pricing.setPriceThreeUnits(new BigDecimal("135800.00"));
    pricing.setPreviousPrice(new BigDecimal("77900.00"));

    // Link pricing to product
    pricing.setProduct(product);
    product.setPricing(pricing);

    // Save and return the product
    return productRepository.save(product);
  }
}

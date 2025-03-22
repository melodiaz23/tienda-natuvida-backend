package com.natuvida.store.service;

import com.natuvida.store.entity.Category;
import com.natuvida.store.entity.Product;
import com.natuvida.store.entity.ProductPricing;
import com.natuvida.store.exception.ValidationException;
import com.natuvida.store.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Service
public class ProductService {
  @Autowired
  ProductRepository productRepository;

  @Autowired
  ProductPricingService productPricingService;

  @Transactional(readOnly = true)
  public List<Product> getAllProducts(){
    return productRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Product> getProductsByCategory(UUID categoryId){
    return productRepository.findByCategoryId(categoryId);
  }

  @Transactional(readOnly = true)
  public Product getProductById(UUID id) {
    return productRepository.getReferenceById(id);
  }

  @Transactional
  public Product saveOrUpdateProduct(UUID id, String name, String description, BigDecimal unitPrice,
                                     BigDecimal priceTwoUnits, BigDecimal priceThreeUnits,
                                     BigDecimal previousPrice, Category category) {

    if (name.isBlank()) {
      throw new ValidationException("Nombre no puede ser vacío");
    }
    if (unitPrice == null) {
      throw new ValidationException("El precio unitario debe contener un valor");
    }

    ProductPricing pricing = productPricingService.setOrUpdatePrices(
        null, unitPrice, priceTwoUnits, priceThreeUnits, previousPrice);

    Product product;

    if (id == null) {
      product = new Product(name);
    } else {
      product = productRepository.findById(id)
          .orElseThrow(() -> new ValidationException("Producto no encontrado"));
      product.setName(name);
    }

    product.setDescription(description);
    product.setPricing(pricing);
    product.setCategory(category);

    return productRepository.save(product);
  }

  @Transactional
  public void deleteProduct(UUID id){
    productRepository.deleteById(id);
  }

}

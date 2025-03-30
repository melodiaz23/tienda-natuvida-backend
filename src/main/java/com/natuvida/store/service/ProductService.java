package com.natuvida.store.service;

import com.natuvida.store.dto.ProductImageDTO;
import com.natuvida.store.dto.response.ProductPricingDTO;
import com.natuvida.store.entity.Category;
import com.natuvida.store.entity.Product;
import com.natuvida.store.entity.ProductImage;
import com.natuvida.store.entity.ProductPricing;
import com.natuvida.store.exception.ValidationException;
import com.natuvida.store.mapper.ProductImageMapper;
import com.natuvida.store.mapper.ProductPricingMapper;
import com.natuvida.store.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
  @Autowired
  ProductRepository productRepository;

  @Autowired
  ProductPricingService productPricingService;

  @Autowired
  ProductImageService productImageService;

  @Autowired
  private ProductImageMapper productImageMapper;

  @Autowired
  private ProductPricingMapper productPricingMapper;

  @Transactional(readOnly = true)
  public List<Product> getAllProducts(){
    return productRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Product> getProductsByCategory(UUID categoryId){
    return productRepository.findByCategoriesId(categoryId);
  }

  @Transactional(readOnly = true)
  public Product getProductById(UUID id) {
    return productRepository.findById(id).orElseThrow(()-> new ValidationException("Producto no encontrado"));
  }


  @Transactional
  public Product saveOrUpdateProduct(UUID id, String name, String description, String preparation,
                                     String ingredients, ProductPricingDTO prices, List<Category> categories,
                                     List<ProductImageDTO> images) {

    if (name.isBlank()) {
      throw new ValidationException("Nombre no puede ser vacío");
    }
    if (prices.getUnitPrice() == null) {
      throw new ValidationException("El precio unitario debe contener un valor");
    }

    ProductPricing pricing = productPricingService.setOrUpdatePrices(productPricingMapper.toEntity(prices));

    Product product;

    if (id == null) {
      product = new Product(name);
    } else {
      product = productRepository.findById(id)
          .orElseThrow(() -> new ValidationException("Producto no encontrado"));
      product.setName(name);
    }

    product.setDescription(description);
    product.setPreparation(preparation);
    product.setIngredients(ingredients);
    product.setPricing(pricing);

    // Actualizar categorías
    if (categories != null) {
      if (product.getCategories() == null) {
        product.setCategories(new ArrayList<>(categories));
      } else {
        product.getCategories().clear();
        product.getCategories().addAll(categories);
      }
    } else if (product.getCategories() != null) {
      product.getCategories().clear();
    }

    product = productRepository.save(product);

    if (images != null) {
      List<ProductImage> productImages = productImageService.saveAll(productImageMapper.toEntityList(images));
      if (product.getImages() == null) {
        product.setImages(new ArrayList<>(productImages));
      } else {
        product.getImages().clear();
        product.getImages().addAll(productImages);
      }
    } else if (product.getImages() != null) {
      product.getImages().clear();
    }

    return productRepository.save(product);
  }

  @Transactional
  public void deleteProduct(UUID id){
    productRepository.deleteById(id);
  }

}

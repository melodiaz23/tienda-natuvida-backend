package com.natuvida.store.service;

import com.natuvida.store.entity.ProductImage;
import com.natuvida.store.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductImageService {

  @Autowired
  private ProductImageRepository productImageRepository;

  public ProductImage save(ProductImage productImage) {
    return productImageRepository.save(productImage);
  }

  public List<ProductImage> saveAll(List<ProductImage> images) {
    return productImageRepository.saveAll(images);
  }

  public Optional<ProductImage> findById(UUID id) {
    return productImageRepository.findById(id);
  }

  public void deleteById(UUID id) {
    productImageRepository.deleteById(id);
  }

}
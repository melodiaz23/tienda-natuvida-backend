package com.natuvida.store.service;

import com.natuvida.store.dto.request.ProductImageRequestDTO;
import com.natuvida.store.entity.Product;
import com.natuvida.store.entity.ProductImage;
import com.natuvida.store.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageService {

  private final ProductImageRepository productImageRepository;

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

  @Transactional
  public List<ProductImage> updateProductImages(Product product, List<ProductImageRequestDTO> newImageDTOs) {
    if ((newImageDTOs == null || newImageDTOs.isEmpty()) &&
        (product.getImages() == null || product.getImages().isEmpty())) {
      return new ArrayList<>();
    }

    if (product.getImages() == null) {
      product.setImages(new ArrayList<>());
    }

    product.getImages().clear();

    if (newImageDTOs == null || newImageDTOs.isEmpty()) {
      return product.getImages();
    }

    // Create and add new images
    for (ProductImageRequestDTO imageDto : newImageDTOs) {
      ProductImage productImage = new ProductImage();
      productImage.setImageUrl(imageDto.getImageUrl());
      productImage.setAltText(imageDto.getAltText());
      productImage.setPrimary(imageDto.getIsPrimary() != null ? imageDto.getIsPrimary() : false);
      productImage.setDisplayOrder(imageDto.getDisplayOrder());

      // Add to the product's collection
      product.getImages().add(productImage);
    }

    return product.getImages();
  }
}
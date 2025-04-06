package com.natuvida.store.service;

import com.natuvida.store.entity.Price;
import com.natuvida.store.entity.Product;
import com.natuvida.store.exception.ValidationException;
import com.natuvida.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final PriceService priceService;

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
  public Product saveOrUpdateProduct(Product productRequest) {
    Product product;
    if (productRequest.getId() == null) {
      product = new Product(productRequest.getName());
    } else {
      product = productRepository.findById(productRequest.getId())
          .orElseThrow(() -> new ValidationException("Producto no encontrado"));
      product.setName(productRequest.getName());
    }

    product.setDescription(productRequest.getDescription());
    product.setPresentation(productRequest.getPresentation());
    product.setIngredients(
        updateList(product.getIngredients(), productRequest.getIngredients()));
    product.setBenefits(
        updateList(product.getBenefits(), productRequest.getBenefits()));
    product.setTags(
        updateList(product.getTags(), productRequest.getTags()));
    product.setUsageMode(productRequest.getUsageMode());

    if (productRequest.getName().isBlank()) {
      throw new ValidationException("Nombre no puede ser vacío");
    }
    if (productRequest.getPrice().getUnit() == null) {
      throw new ValidationException("El precio unitario debe contener un valor");
    }

    Price prices = priceService.setOrUpdatePrices(productRequest.getPrice());
    product.setPrice(prices);
    product.setCategories(
        updateList(product.getCategories(), productRequest.getCategories()));

    product = productRepository.save(product);

    product.setImages(
        updateList(product.getImages(), productRequest.getImages()));

    return productRepository.save(product);
  }

  @Transactional
  public void deleteProduct(UUID id){
    productRepository.deleteById(id);
  }


  private <T> List<T> updateList(List<T> currentList, List<T> newList){
    if (newList != null) {
      if (currentList == null) {
        currentList = new ArrayList<>(newList);
      } else {
        currentList.clear();
        currentList.addAll(newList);
      }
    } else if (currentList != null) {
      currentList.clear();
    }
    return currentList;
  }

}

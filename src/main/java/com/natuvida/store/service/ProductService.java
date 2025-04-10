package com.natuvida.store.service;
import com.natuvida.store.dto.request.ProductRequestDTO;
import com.natuvida.store.dto.response.ProductDTO;
import com.natuvida.store.entity.Category;
import com.natuvida.store.entity.Price;
import com.natuvida.store.entity.Product;
import com.natuvida.store.exception.ValidationException;
import com.natuvida.store.mapper.CategoryMapper;
import com.natuvida.store.mapper.ProductMapper;
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
  private final CategoryMapper categoryMapper;
  private final ProductMapper productMapper;

  @Transactional(readOnly = true)
  public List<ProductDTO> getAllProducts(){
    return productMapper.toDtoList(productRepository.findAll());
  }

  @Transactional(readOnly = true)
  public List<ProductDTO> getProductsByCategory(UUID categoryId){
    return productMapper.toDtoList(productRepository.findByCategoriesId(categoryId));
  }

  @Transactional(readOnly = true)
  public Product getProductById(UUID id) {
    return productRepository.findById(id).orElseThrow(()-> new ValidationException("Producto no encontrado"));
  }

  @Transactional
  public ProductDTO saveOrUpdateProduct(ProductRequestDTO productRequest) {
    Product product;
    if (productRequest.getId() == null) {
      product = new Product(productRequest.getName());
    } else {
      product = productRepository.findById(productRequest.getId())
          .orElseThrow(() -> new ValidationException("Producto no encontrado"));
      product.setName(productRequest.getName());
    }

    if (productRequest.getCustomName() != null && !productRequest.getCustomName().trim().isEmpty()) {
      product.setSlug(generateSlug(productRequest.getCustomName(), productRequest.getId()));
    } else if (product.getSlug() == null || product.getSlug().isEmpty()) {
      product.setSlug(generateSlug(productRequest.getName(), productRequest.getId()));
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

    if (productRequest.getCategories() != null) {
      List<Category> categoryEntities = categoryMapper.toEntityList(productRequest.getCategories());
      product.setCategories(
          updateList(product.getCategories(), categoryEntities));
    }

    product.setImages(
        updateList(product.getImages(), productRequest.getImages()));

    return productMapper.toDto(productRepository.save(product));
  }

  @Transactional
  public void deleteProduct(UUID id){
    productRepository.deleteById(id);
  }

  private String generateSlug(String customName, UUID id) {
    if (customName == null || customName.trim().isEmpty()) {
      throw new ValidationException("El nombre personalizado no puede estar vacío");
    }

    String slug = customName.toLowerCase()
        .replaceAll("[^a-z0-9\\s-áéíóúñ]", "")
        .replaceAll("[áéíóúñ]", "aeioun")
        .replaceAll("\\s+", "-")
        .replaceAll("-+", "-")
        .trim();

    // Verificar unicidad
    int counter = 0;
    String baseSlug = slug;
    UUID currentId = id != null ? id : UUID.randomUUID();

    while (productRepository.existsBySlugAndIdNot(slug, currentId)) {
      counter++;
      slug = baseSlug + "-" + counter;
    }

    return slug;
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

package com.natuvida.store.service;

import com.natuvida.store.dto.request.ProductRequestDTO;
import com.natuvida.store.dto.response.ProductResponseDTO;
import com.natuvida.store.entity.Category;
import com.natuvida.store.entity.Price;
import com.natuvida.store.entity.Product;
import com.natuvida.store.exception.ValidationException;
import com.natuvida.store.mapper.CategoryMapper;
import com.natuvida.store.mapper.ProductMapper;
import com.natuvida.store.repository.CategoryRepository;
import com.natuvida.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final PriceService priceService;
  private final CategoryMapper categoryMapper;
  private final ProductMapper productMapper;
  private final CategoryService categoryService;
  private final ProductImageService productImageService;
  private final CategoryRepository categoryRepository;

  @Transactional(readOnly = true)
  public List<ProductResponseDTO> getAllProducts() {
    return productMapper.toDtoList(productRepository.findAll());
  }

  @Transactional(readOnly = true)
  public List<ProductResponseDTO> getProductsByCategory(UUID categoryId) {
    return productMapper.toDtoList(productRepository.findByCategoriesId(categoryId));
  }

  @Transactional(readOnly = true)
  public ProductResponseDTO getProductById(UUID id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ValidationException("Producto no encontrado"));
    return productMapper.toDto(product);
  }

  @Transactional(readOnly = true)
  public ProductResponseDTO getProductBySlug(String slug) {
    Product product = productRepository.findBySlug(slug)
        .orElseThrow(() -> new ValidationException("Producto no encontrado"));
    return productMapper.toDto(product);
  }

  @Transactional
  public ProductResponseDTO createProduct(ProductRequestDTO productRequest) {
    if (productRequest.getId() != null) {
      throw new ValidationException("Para crear un producto, el ID debe ser nulo");
    }
    Product product = new Product(productRequest.getName());
    return processAndSaveProduct(product, productRequest);
  }

  @Transactional
  public ProductResponseDTO updateProduct(ProductRequestDTO productRequest) {
    Product existingProduct = productRepository.findById(productRequest.getId())
        .orElseThrow(() -> new ValidationException("Producto no encontrado"));
    if (productRequest.getImages() != null) {
      existingProduct.getImages().clear();
      existingProduct.getImages().addAll(productImageService.updateProductImages(existingProduct, productRequest.getImages()));
    }

    existingProduct.setName(productRequest.getName());
    return processAndSaveProduct(existingProduct, productRequest);
  }


  @Transactional
  public void deleteProduct(UUID id) {
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

  private <T> List<T> updateList(List<T> currentList, List<T> newList) {
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

  private ProductResponseDTO processAndSaveProduct(Product product, ProductRequestDTO productRequest) {
    // Configurar slug
    if (productRequest.getCustomName() != null && !productRequest.getCustomName().trim().isEmpty()) {
      product.setSlug(generateSlug(productRequest.getCustomName(), product.getId()));
    } else if (product.getSlug() == null || product.getSlug().isEmpty()) {
      product.setSlug(generateSlug(productRequest.getName(), product.getId()));
    }

    // Validaciones
    if (productRequest.getName().isBlank()) {
      throw new ValidationException("Nombre no puede ser vacío");
    }
    if (productRequest.getPrice() == null || productRequest.getPrice().getUnit() == null) {
      throw new ValidationException("El precio unitario debe contener un valor");
    }

    // Configurar propiedades del producto
    product.setCustomName(productRequest.getCustomName());
    product.setDescription(productRequest.getDescription());
    product.setPresentation(productRequest.getPresentation());
    product.setIngredients(updateList(product.getIngredients(), productRequest.getIngredients()));
    product.setBenefits(updateList(product.getBenefits(), productRequest.getBenefits()));
    product.setTags(updateList(product.getTags(), productRequest.getTags()));
    product.setBonuses(updateList(product.getBonuses(), productRequest.getBonuses()));
    product.setContraindications(updateList(product.getContraindications(), productRequest.getContraindications()));
    product.setUsageMode(productRequest.getUsageMode());

    // Configurar precio
    Price prices = priceService.setOrUpdatePrices(productRequest.getPrice());
    product.setPrice(prices);

    // --- Bloque Corregido de Categorías ---
    if (productRequest.getCategories() != null && !productRequest.getCategories().isEmpty()) {
      List<UUID> categoryIds = productRequest.getCategories().stream()
          .filter(Objects::nonNull).distinct().collect(Collectors.toList());

      List<Category> categoryEntities = new ArrayList<>(); // Lista para entidades REALES

      if (!categoryIds.isEmpty()) {
        categoryEntities = categoryRepository.findAllById(categoryIds);
        if (categoryEntities.size() != categoryIds.size()) {
          System.out.println("ADVERTENCIA: Se solicitaron IDs de categoría que no existen.");
          // Considera lanzar una excepción si es un error crítico
          // throw new ValidationException("Se proporcionaron IDs de categoría inválidos.");
        }
      }
      updateList(product.getCategories(), categoryEntities);

    } else {

      updateList(product.getCategories(), new ArrayList<>());
    }
    // Usar el servicio específico para actualizar las imágenes
    product.setImages(productImageService.updateProductImages(product, productRequest.getImages()));

    // Guardar y devolver DTO
    return productMapper.toDto(productRepository.save(product));
  }


}

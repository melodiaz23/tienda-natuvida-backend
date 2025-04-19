package com.natuvida.store.service;

import com.natuvida.store.dto.request.ProductRequestDTO;
import com.natuvida.store.dto.response.CartResponseDTO;
import com.natuvida.store.dto.response.CategoryResponseDTO;
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

  @Transactional(readOnly = true)
  public List<ProductDTO> getAllProducts() {
    return productMapper.toDtoList(productRepository.findAll());
  }

  @Transactional(readOnly = true)
  public List<ProductDTO> getProductsByCategory(UUID categoryId) {
    return productMapper.toDtoList(productRepository.findByCategoriesId(categoryId));
  }

  @Transactional(readOnly = true)
  public ProductDTO getProductById(UUID id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ValidationException("Producto no encontrado"));
    return productMapper.toDto(product);
  }

  @Transactional(readOnly = true)
  public ProductDTO getProductBySlug(String slug) {
    Product product = productRepository.findBySlug(slug)
        .orElseThrow(() -> new ValidationException("Producto no encontrado"));
    return productMapper.toDto(product);
  }

  @Transactional
  public ProductDTO createProduct(ProductRequestDTO productRequest) {
    if (productRequest.getId() != null) {
      throw new ValidationException("Para crear un producto, el ID debe ser nulo");
    }
    Product product = new Product(productRequest.getName());
    return processAndSaveProduct(product, productRequest);
  }

  @Transactional
  public ProductDTO updateProduct(UUID id, ProductRequestDTO productRequest) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ValidationException("Producto no encontrado"));
    product.setName(productRequest.getName());
    return processAndSaveProduct(product, productRequest);
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

  private ProductDTO processAndSaveProduct(Product product, ProductRequestDTO productRequest) {
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
    product.setDescription(productRequest.getDescription());
    product.setPresentation(productRequest.getPresentation());
    product.setIngredients(updateList(product.getIngredients(), productRequest.getIngredients()));
    product.setBenefits(updateList(product.getBenefits(), productRequest.getBenefits()));
    product.setTags(updateList(product.getTags(), productRequest.getTags()));
    product.setUsageMode(productRequest.getUsageMode());

    // Configurar precio
    Price prices = priceService.setOrUpdatePrices(productRequest.getPrice());
    product.setPrice(prices);

    // --- Reemplazo para el bloque de categorías ---
    if (productRequest.getCategories() != null && !productRequest.getCategories().isEmpty()) {
      List<UUID> categoryIds = productRequest.getCategories().stream()
          .filter(Objects::nonNull)
          .distinct()
          .collect(Collectors.toList());

      List<CategoryResponseDTO> categoryDto; // Declara la lista para guardar las entidades encontradas

      if (!categoryIds.isEmpty()) {
        categoryDto = categoryService.findAllById(categoryIds);

        // 4. (Opcional pero MUY recomendado) Verifica si se encontraron todas las categorías solicitadas.
        if (categoryDto.size() != categoryIds.size()) {
          System.out.println("ADVERTENCIA: Se solicitaron IDs de categoría que no existen.");
        }
      } else {
        categoryDto = new ArrayList<>();
      }
      product.setCategories(categoryMapper.toEntityList(categoryDto));

    } else {
      product.setCategories(new ArrayList<>());
    }
    product.setImages(updateList(product.getImages(), productRequest.getImages()));

    // Guardar y devolver DTO
    return productMapper.toDto(productRepository.save(product));
  }

}

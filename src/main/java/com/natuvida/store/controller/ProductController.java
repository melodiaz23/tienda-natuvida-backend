package com.natuvida.store.controller;

import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.dto.response.ProductDTO;
import com.natuvida.store.dto.request.ProductRequestDTO;
import com.natuvida.store.entity.Category;
import com.natuvida.store.entity.Product;
import com.natuvida.store.mapper.ProductMapper;
import com.natuvida.store.service.CategoryService;
import com.natuvida.store.service.ProductService;
import com.natuvida.store.util.ApiPaths;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(ApiPaths.PRODUCTS)
public class ProductController {

  private ProductService productService;
  private CategoryService categoryService;
  private ProductMapper productMapper;

  @PostMapping
  public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@RequestBody ProductRequestDTO request) {
    List<Category> categories = new ArrayList<>();
    if (request.getCategories() != null && !request.getCategories().isEmpty()) {
      categories = request.getCategories().stream()
          .filter(category -> category != null && category.getId() != null)
          .map(category -> categoryService.getCategoryById(category.getId()))
          .toList();
    }


    Product product = productMapper.toEntity(request);
    product.setCategories(categories);

    ProductDTO productDTO = productMapper.toDto(productService.saveOrUpdateProduct(product));
    return ResponseEntity.ok(ApiResponse.success(productDTO, "Producto creado exitosamente"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@PathVariable UUID id, @RequestBody ProductRequestDTO request) {
    List<Category> categories = new ArrayList<>();

    if (request.getCategories() != null && !request.getCategories().isEmpty()) {
      categories = request.getCategories().stream()
          .filter(category -> category != null && category.getId() != null)
          .map(category -> categoryService.getCategoryById(category.getId()))
          .toList();
    }

    Product product = productMapper.toEntity(request);
    product.setId(id); // Ensure ID is set for update
    product.setCategories(categories);

    ProductDTO productDTO = productMapper.toDto(productService.saveOrUpdateProduct(product));
    return ResponseEntity.ok(ApiResponse.success(productDTO, "Producto actualizado exitosamente"));
  }
  @GetMapping
  public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
    List<Product> products = productService.getAllProducts();
    List<ProductDTO> productDTOs = productMapper.toDtoList(products);
    return ResponseEntity.ok(ApiResponse.success(productDTOs, "Consulta exitosa"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable UUID id) {
    Product product = productService.getProductById(id);
    ProductDTO productDTO = productMapper.toDto(product);
    return ResponseEntity.ok(ApiResponse.success(productDTO, "Consulta exitosa."));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
    productService.deleteProduct(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Producto eliminado exitosamente"));
  }
}
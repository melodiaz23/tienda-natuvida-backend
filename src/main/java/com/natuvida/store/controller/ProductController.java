package com.natuvida.store.controller;

import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.dto.response.ProductResponseDTO;
import com.natuvida.store.dto.request.ProductRequestDTO;
import com.natuvida.store.service.ProductService;
import com.natuvida.store.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.PRODUCTS)
public class ProductController {

  private final ProductService productService;

  @PostMapping
  public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(@RequestBody ProductRequestDTO request) {
    ProductResponseDTO newProduct = productService.createProduct(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(newProduct, "Producto creado exitosamente"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(@PathVariable UUID id, @RequestBody ProductRequestDTO request) {
    ProductResponseDTO updatedProduct = productService.updateProduct(request);
    return ResponseEntity.ok(ApiResponse.success(updatedProduct, "Producto actualizado exitosamente"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAllProducts() {
    List<ProductResponseDTO> productResponseDTOS = productService.getAllProducts();
    return ResponseEntity.ok(ApiResponse.success(productResponseDTOS, "Consulta exitosa"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(@PathVariable UUID id) {
    ProductResponseDTO productResponseDTO = productService.getProductById(id);
    return ResponseEntity.ok(ApiResponse.success(productResponseDTO, "Consulta exitosa"));
  }

  @GetMapping("/slug/{slug}")
  public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductBySlug(@PathVariable String slug) {
    ProductResponseDTO productResponseDTO = productService.getProductBySlug(slug);
    return ResponseEntity.ok(ApiResponse.success(productResponseDTO, "Consulta exitosa"));
  }

  @GetMapping("/category/{categoryId}")
  public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getProductsByCategory(@PathVariable UUID categoryId) {
    List<ProductResponseDTO> productResponseDTOS = productService.getProductsByCategory(categoryId);
    return ResponseEntity.ok(ApiResponse.success(productResponseDTOS, "Consulta exitosa"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
    productService.deleteProduct(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Producto eliminado exitosamente"));
  }
}
package com.natuvida.store.controller;

import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.dto.response.ProductDTO;
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
  public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@RequestBody ProductRequestDTO request) {
    ProductDTO newProduct = productService.createProduct(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(newProduct, "Producto creado exitosamente"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@PathVariable UUID id, @RequestBody ProductRequestDTO request) {
    ProductDTO updatedProduct = productService.updateProduct(id, request);
    return ResponseEntity.ok(ApiResponse.success(updatedProduct, "Producto actualizado exitosamente"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
    List<ProductDTO> productDTOs = productService.getAllProducts();
    return ResponseEntity.ok(ApiResponse.success(productDTOs, "Consulta exitosa"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable UUID id) {
    ProductDTO productDTO = productService.getProductById(id);
    return ResponseEntity.ok(ApiResponse.success(productDTO, "Consulta exitosa"));
  }

  @GetMapping("/{slug}")
  public ResponseEntity<ApiResponse<ProductDTO>> getProductBySlug(@PathVariable String slug) {
    ProductDTO productDTO = productService.getProductBySlug(slug);
    return ResponseEntity.ok(ApiResponse.success(productDTO, "Consulta exitosa"));
  }

  @GetMapping("/category/{categoryId}")
  public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByCategory(@PathVariable UUID categoryId) {
    List<ProductDTO> productDTOs = productService.getProductsByCategory(categoryId);
    return ResponseEntity.ok(ApiResponse.success(productDTOs, "Consulta exitosa"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
    productService.deleteProduct(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Producto eliminado exitosamente"));
  }
}
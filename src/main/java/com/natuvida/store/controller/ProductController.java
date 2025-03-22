package com.natuvida.store.controller;


import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.dto.ProductRequestDTO;
import com.natuvida.store.entity.Category;
import com.natuvida.store.entity.Product;
import com.natuvida.store.service.CategoryService;
import com.natuvida.store.service.ProductService;
import com.natuvida.store.util.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.PRODUCTS)
public class ProductController {

  @Autowired
  ProductService productService;

  @Autowired
  CategoryService categoryService;

  @PostMapping
  public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody ProductRequestDTO request){
    Category category = categoryService.getCategoryById(request.getCategoryId());

    Product product = productService.saveOrUpdateProduct(
        null,
        request.getName(),
        request.getDescription(),
        request.getUnitPrice(),
        request.getPriceTwoUnits(),
        request.getPriceThreeUnits(),
        request.getPreviousPrice(),
        category
    );

    return ResponseEntity.ok(ApiResponse.success(product, "Producto creado exitosamente"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable UUID id, @RequestBody ProductRequestDTO request){
    Category category = categoryService.getCategoryById(request.getCategoryId());

    Product product = productService.saveOrUpdateProduct(
        id,
        request.getName(),
        request.getDescription(),
        request.getUnitPrice(),
        request.getPriceTwoUnits(),
        request.getPriceThreeUnits(),
        request.getPreviousPrice(),
        category
    );

    return ResponseEntity.ok(ApiResponse.success(product, "Producto actualizado exitosamente"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
    return ResponseEntity.ok(ApiResponse.success(productService.getAllProducts(), "Consulta exitosa"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable UUID id) {
    return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id), "Consulta exitosa."));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id){
    productService.deleteProduct(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Producto eliminado exitosamente"));
  }


}

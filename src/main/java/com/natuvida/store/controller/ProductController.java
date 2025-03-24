package com.natuvida.store.controller;

import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.dto.ProductDTO;
import com.natuvida.store.dto.ProductRequestDTO;
import com.natuvida.store.entity.Category;
import com.natuvida.store.entity.Product;
import com.natuvida.store.mapper.ProductMapper;
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

  @Autowired
  private ProductMapper productMapper;

  @PostMapping
  public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@RequestBody ProductRequestDTO request){
    Category category = null;
    if (request.getCategoryId() != null) {
      category = categoryService.getCategoryById(request.getCategoryId());
    }

    Product product = productService.saveOrUpdateProduct(
        null,
        request.getName(),
        request.getDescription(),
        request.getPreparation(),
        request.getIngredients(),
        request.getPricing(),
        category,
        request.getImages()
    );

    ProductDTO productDTO = productMapper.toDto(product);
    return ResponseEntity.ok(ApiResponse.success(productDTO, "Producto creado exitosamente"));
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

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@PathVariable UUID id, @RequestBody ProductRequestDTO request){
    Category category = null;
    if (request.getCategoryId() != null) {
      category = categoryService.getCategoryById(request.getCategoryId());
    }

    Product product = productService.saveOrUpdateProduct(
        id,
        request.getName(),
        request.getDescription(),
        request.getPreparation(),
        request.getIngredients(),
        request.getPricing(),
        category,
        request.getImages()
    );
    ProductDTO productDTO = productMapper.toDto(product);

    return ResponseEntity.ok(ApiResponse.success(productDTO, "Producto actualizado exitosamente"));
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id){
    productService.deleteProduct(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Producto eliminado exitosamente"));
  }


}

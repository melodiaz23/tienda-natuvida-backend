package com.natuvida.store.controller;

import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.dto.response.CategoryResponseDTO;
import com.natuvida.store.dto.request.CategoryRequestDTO;
import com.natuvida.store.entity.Category;
import com.natuvida.store.mapper.CategoryMapper;
import com.natuvida.store.service.CategoryService;
import com.natuvida.store.util.ApiPaths;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.CATEGORIES)
public class CategoryController {

  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

  @PostMapping
  public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(@RequestBody CategoryRequestDTO request){
    Category category = categoryService.saveOrUpdateCategory(null, request.getName(), request.getDescription());
    CategoryResponseDTO categoryResponseDTO = categoryMapper.toDto(category);
    return ResponseEntity.ok(ApiResponse.success(categoryResponseDTO, "Categoría creada exitosamente"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getAllCategories(){
    List<CategoryResponseDTO> categoryResponseDTOS = categoryService.getAllCategories();
    return ResponseEntity.ok(ApiResponse.success(categoryResponseDTOS, "Consulta exitosa"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CategoryResponseDTO>> getCategoryById(@PathVariable UUID id){
    Category category = categoryService.getCategoryById(id);
    CategoryResponseDTO categoryResponseDTO = categoryMapper.toDto(category);
    return ResponseEntity.ok(ApiResponse.success(categoryResponseDTO, "Consulta exitosa"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryRequestDTO request) {
  Category category = categoryService.saveOrUpdateCategory(id, request.getName(), request.getDescription());
  CategoryResponseDTO categoryResponseDTO = categoryMapper.toDto(category);
  return ResponseEntity.ok(ApiResponse.success(categoryResponseDTO, "Categoría Actualizada exitosamente"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id){
    categoryService.deleteCategory(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Categoría eliminada exitosamente."));
  }

}

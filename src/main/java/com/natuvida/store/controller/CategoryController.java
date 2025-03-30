package com.natuvida.store.controller;

import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.dto.response.CategoryDTO;
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

  private CategoryService categoryService;
  private CategoryMapper categoryMapper;

  @PostMapping
  public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@RequestBody CategoryRequestDTO request){
    Category category = categoryService.saveOrUpdateCategory(null, request.getName(), request.getDescription());
    CategoryDTO categoryDTO = categoryMapper.toDto(category);
    return ResponseEntity.ok(ApiResponse.success(categoryDTO, "Categoría creada exitosamente"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories(){
    List<Category> categories = categoryService.getAllCategories();
    List<CategoryDTO> categoryDTOs = categoryMapper.toDtoList(categories);
    return ResponseEntity.ok(ApiResponse.success(categoryDTOs, "Consulta exitosa"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(@PathVariable UUID id){
    Category category = categoryService.getCategoryById(id);
    CategoryDTO categoryDTO = categoryMapper.toDto(category);
    return ResponseEntity.ok(ApiResponse.success(categoryDTO, "Consulta exitosa"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryRequestDTO request) {
  Category category = categoryService.saveOrUpdateCategory(id, request.getName(), request.getDescription());
  CategoryDTO categoryDTO = categoryMapper.toDto(category);
  return ResponseEntity.ok(ApiResponse.success(categoryDTO, "Categoría Actualizada exitosamente"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id){
    categoryService.deleteCategory(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Categoría eliminada exitosamente."));
  }

}

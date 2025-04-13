package com.natuvida.store.service;

import com.natuvida.store.dto.response.CategoryResponseDTO;
import com.natuvida.store.entity.Category;
import com.natuvida.store.exception.ValidationException;
import com.natuvida.store.mapper.CategoryMapper;
import com.natuvida.store.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  @Transactional(readOnly = true)
  public List<CategoryResponseDTO> getAllCategories(){
    return categoryMapper.toDtoList(categoryRepository.findAll());
  }

  @Transactional(readOnly = true)
  public Category getCategoryById(UUID id){
    return categoryRepository.getReferenceById(id);
  }

  @Transactional
  public void deleteCategory(UUID id){
    categoryRepository.deleteById(id);
  }

  @Transactional
  public Category saveOrUpdateCategory(UUID id, String name, String description){
    if (name.isBlank()) throw new ValidationException("Categoría no existe");
    Category category;
    if (id == null){
      category = new Category();
    } else {
      category = categoryRepository.getReferenceById(id);
    }
    category.setName(name);
    category.setDescription(description);
    return categoryRepository.save(category);
  }
}

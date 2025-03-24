package com.natuvida.store.service;

import com.natuvida.store.entity.Category;
import com.natuvida.store.exception.ValidationException;
import com.natuvida.store.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

  @Autowired
  CategoryRepository categoryRepository;

  @Transactional(readOnly = true)
  public List<Category> getAllCategories(){
    return categoryRepository.findAll();
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

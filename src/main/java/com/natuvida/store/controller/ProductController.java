package com.natuvida.store.controller;


import com.natuvida.store.entity.Product;
import com.natuvida.store.service.ProductService;
import com.natuvida.store.util.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.PRODUCTS)
public class ProductController {

  @Autowired
  ProductService productService;

  @GetMapping
  public List<Product> getAllProducts(){
    return productService.getAllProducts();
  }

  @GetMapping("/test-add")  // Changed from PostMapping to GetMapping
  public Product createTestProduct() {
    return productService.createProduct();
  }



}

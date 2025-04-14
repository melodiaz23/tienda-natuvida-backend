package com.natuvida.store.exception.product;

import java.util.UUID;

public class ProductNotFoundException extends ProductException {
  public ProductNotFoundException(String message) {
    super(message);
  }

  public ProductNotFoundException(UUID productId) {
    super("Producto no encontrado con ID: " + productId);
  }
}

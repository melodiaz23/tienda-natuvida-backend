package com.natuvida.store.exception.product;

// Para errores de validación de producto
public class ProductValidationException extends ProductException {
  public ProductValidationException(String message) {
    super(message);
  }
}

package com.natuvida.store.exception.product;

// Para cuando un producto no está disponible o habilitado
public class ProductNotAvailableException extends ProductException {
  public ProductNotAvailableException(String message) {
    super(message);
  }
}

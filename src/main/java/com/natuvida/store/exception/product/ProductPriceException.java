package com.natuvida.store.exception.product;

// Para errores relacionados con el precio del producto
public class ProductPriceException extends ProductException {
  public ProductPriceException(String message) {
    super(message);
  }
}

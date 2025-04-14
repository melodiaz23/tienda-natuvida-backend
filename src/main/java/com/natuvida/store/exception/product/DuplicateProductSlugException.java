package com.natuvida.store.exception.product;

// Para errores de slug único
public class DuplicateProductSlugException extends ProductException {
  public DuplicateProductSlugException(String slug) {
    super("Ya existe un producto con el slug: " + slug);
  }
}

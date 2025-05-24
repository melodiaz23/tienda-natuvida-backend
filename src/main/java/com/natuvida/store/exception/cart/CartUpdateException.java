package com.natuvida.store.exception.cart;

public class CartUpdateException extends RuntimeException {
  public CartUpdateException(String message) {
    super("Error al actualizar el carrito: " + message);
  }
}

package com.natuvida.store.controller;

import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.dto.request.CartItemRequestDTO;
import com.natuvida.store.dto.request.CartRequestDTO;
import com.natuvida.store.dto.response.CartResponseDTO;
import com.natuvida.store.service.CartService;
import com.natuvida.store.util.ApiPaths;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.CART)
public class CartController {

  private final CartService cartService;

  @GetMapping
  public ResponseEntity<ApiResponse<CartResponseDTO>> getCurrentCart(HttpSession session) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UUID userId = null;
    // Si está autenticado, obtener el ID del usuario
    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
      userId = UUID.fromString(auth.getName());
    }
    // Intenta encontrar carrito por userId
    CartResponseDTO cart = cartService.findCartByUserId(userId);

    if (cart != null) {
      return ResponseEntity.ok(ApiResponse.success(cart, "Carrito recuperado exitosamente"));
    } else {
      // No hay carrito existente, crear uno nuevo
      CartRequestDTO newCartRequest = new CartRequestDTO();
      newCartRequest.setUserId(userId);
      cart = cartService.createCart(newCartRequest);
      return ResponseEntity.ok(ApiResponse.success(cart, "Nuevo carrito creado"));
    }
  }

  @PostMapping("/items")
  public ResponseEntity<ApiResponse<CartResponseDTO>> addItemToCart(
      @RequestBody CartItemRequestDTO itemRequest,
      HttpSession session) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UUID userId = null;

    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
      userId = UUID.fromString(auth.getName());
    }

    // Obtener o crear carrito
    CartResponseDTO cart = cartService.findCartByUserId(userId);

    if (cart == null) {
      CartRequestDTO newCartRequest = new CartRequestDTO();
      newCartRequest.setUserId(userId);
      cart = cartService.createCart(newCartRequest);
    }

    // Agregar item al carrito
    CartResponseDTO updatedCart = cartService.addItemToCart(cart.getId(), itemRequest);
    return ResponseEntity.ok(ApiResponse.success(updatedCart, "Producto agregado al carrito"));
  }

  @PutMapping("/items/{itemId}")
  public ResponseEntity<ApiResponse<CartResponseDTO>> updateCartItemQuantity(
      @PathVariable UUID itemId,
      @RequestParam int quantity,
      HttpSession session) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UUID userId = null;

    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
      userId = UUID.fromString(auth.getName());
    }

    CartResponseDTO cart = cartService.findCartByUserId(userId);

    if (cart == null) {
      return ResponseEntity.badRequest().body(ApiResponse.error("No se encontró un carrito activo"));
    }

    CartResponseDTO updatedCart = cartService.updateCartItemQuantity(cart.getId(), itemId, quantity);
    return ResponseEntity.ok(ApiResponse.success(updatedCart, "Cantidad actualizada"));
  }

  @DeleteMapping("/items/{itemId}")
  public ResponseEntity<ApiResponse<CartResponseDTO>> removeItemFromCart(
      @PathVariable UUID itemId,
      HttpSession session) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UUID userId = null;

    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
      userId = UUID.fromString(auth.getName());
    }

    CartResponseDTO cart = cartService.findCartByUserId(userId);

    if (cart == null) {
      return ResponseEntity.badRequest().body(ApiResponse.error("No se encontró un carrito activo"));
    }

    cartService.removeItemFromCart(cart.getId(), itemId);

    // Obtener el carrito actualizado
    CartResponseDTO updatedCart = cartService.findCartById(cart.getId());
    return ResponseEntity.ok(ApiResponse.success(updatedCart, "Producto eliminado del carrito"));
  }

  @DeleteMapping
  public ResponseEntity<ApiResponse<Void>> clearCart(HttpSession session) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UUID userId = null;

    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
      userId = UUID.fromString(auth.getName());
    }

    CartResponseDTO cart = cartService.findCartByUserId(userId);

    if (cart == null) {
      return ResponseEntity.badRequest().body(ApiResponse.error("No se encontró un carrito activo"));
    }

    cartService.clearCart(cart.getId());
    return ResponseEntity.ok(ApiResponse.success(null, "Carrito vaciado exitosamente"));
  }
}
package com.natuvida.store.controller;

import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.dto.request.CartItemRequestDTO;
import com.natuvida.store.dto.response.CartResponseDTO;
import com.natuvida.store.service.CartService;
import com.natuvida.store.service.UserService;
import com.natuvida.store.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.CART)
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;
  private final UserService userService;

  @GetMapping
  public ResponseEntity<ApiResponse<CartResponseDTO>> getCurrentCart() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
      UUID userId = userService.getUserIdByEmail(auth.getName());
      CartResponseDTO cart = cartService.findCartByUserId(userId);

      if (cart != null) {
        return ResponseEntity.ok(ApiResponse.success(cart, "Carrito recuperado exitosamente"));
      } else {
        cart = cartService.createCart(userId);
        return ResponseEntity.ok(ApiResponse.success(cart, "Nuevo carrito creado"));
      }
    } else {
      return ResponseEntity.ok(ApiResponse.success(null, "Usuario no autenticado, use carrito local"));
    }
  }

  @PostMapping("/sync")
  public ResponseEntity<ApiResponse<CartResponseDTO>> syncCartFromLocalStorage(
      @RequestBody List<CartItemRequestDTO> localCartItems) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error("Debe iniciar sesión para sincronizar el carrito"));
    }
    UUID userId = userService.getUserIdByEmail(auth.getName());
    CartResponseDTO syncedCart = cartService.syncCartFromLocalStorage(userId, localCartItems);
    return ResponseEntity.ok(ApiResponse.success(syncedCart, "Carrito sincronizado exitosamente"));
  }

  @PostMapping("/items")
  public ResponseEntity<ApiResponse<CartResponseDTO>> addItemToCart(@RequestBody CartItemRequestDTO itemRequest) {
    // Verificar que el usuario esté autenticado
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error("Debe iniciar sesión para modificar el carrito"));
    }

    UUID userId = userService.getUserIdByEmail(auth.getName());
    CartResponseDTO cart = cartService.findCartByUserId(userId);

    if (cart == null) {
      CartResponseDTO newCart = cartService.createCart(userId);
      CartResponseDTO updatedCart = cartService.addItemToCart(newCart.getId(), itemRequest);
      return ResponseEntity.ok(ApiResponse.success(updatedCart, "Producto agregado al carrito"));
    }

    CartResponseDTO updatedCart = cartService.addItemToCart(cart.getId(), itemRequest);
    return ResponseEntity.ok(ApiResponse.success(updatedCart, "Producto agregado al carrito"));
  }

  @PutMapping("/items/{itemId}")
  public ResponseEntity<ApiResponse<CartResponseDTO>> updateCartItem(
      @PathVariable UUID itemId,
      @RequestParam int quantity) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error("Debe iniciar sesión para modificar el carrito"));
    }
    UUID userId = userService.getUserIdByEmail(auth.getName());
    CartResponseDTO cart = cartService.findCartByUserId(userId);

    if (cart == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("No se encontró un carrito para el usuario"));
    }

    CartResponseDTO updatedCart = cartService.updateCartItemQuantity(cart.getId(), itemId, quantity);
    return ResponseEntity.ok(ApiResponse.success(updatedCart, "Carrito actualizado exitosamente"));
  }

  @DeleteMapping("/items/{itemId}")
  public ResponseEntity<ApiResponse<CartResponseDTO>> removeCartItem(@PathVariable UUID itemId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error("Debe iniciar sesión para modificar el carrito"));
    }

    UUID userId = userService.getUserIdByEmail(auth.getName());
    CartResponseDTO cart = cartService.findCartByUserId(userId);

    if (cart == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("No se encontró un carrito para el usuario"));
    }

    cartService.removeItemFromCart(cart.getId(), itemId);
    CartResponseDTO updatedCart = cartService.findCartById(cart.getId());
    return ResponseEntity.ok(ApiResponse.success(updatedCart, "Producto eliminado del carrito"));
  }

  @DeleteMapping("/clear")
  public ResponseEntity<ApiResponse<CartResponseDTO>> clearCart() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error("Debe iniciar sesión para modificar el carrito"));
    }

    UUID userId = userService.getUserIdByEmail(auth.getName());
    CartResponseDTO cart = cartService.findCartByUserId(userId);

    if (cart == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("No se encontró un carrito para el usuario"));
    }
    cartService.clearCart(cart.getId());
    CartResponseDTO emptyCart = cartService.findCartById(cart.getId());
    return ResponseEntity.ok(ApiResponse.success(emptyCart, "Carrito vaciado exitosamente"));
  }
}
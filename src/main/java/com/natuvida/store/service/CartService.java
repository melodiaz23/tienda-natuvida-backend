package com.natuvida.store.service;

import com.natuvida.store.dto.request.CartItemRequestDTO;
import com.natuvida.store.dto.response.CartResponseDTO;
import com.natuvida.store.entity.*;
import com.natuvida.store.enums.CartStatus;
import com.natuvida.store.exception.ValidationException;
import com.natuvida.store.exception.cart.CartValidationException;
import com.natuvida.store.mapper.CartMapper;
import com.natuvida.store.repository.CartItemRepository;
import com.natuvida.store.repository.CartRepository;
import com.natuvida.store.repository.ProductRepository;
import com.natuvida.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final CartMapper cartMapper;

  @Transactional(readOnly = true)
  public CartResponseDTO findCartById(UUID id) {
    Cart cart = cartRepository.findById(id)
        .orElseThrow(() -> new ValidationException("Carrito no encontrado"));
    return cartMapper.toDto(cart);
  }

  @Transactional(readOnly = true)
  public CartResponseDTO findCartByUserId(UUID userId) {
    if (userId != null) {
      Optional<Cart> userCart = cartRepository.findByUserIdAndStatusAndEnabled(userId, CartStatus.ACTIVE, true);
      if (userCart.isPresent()) {
        return cartMapper.toDto(userCart.get());
      }
    }
    return null;
  }

  @Transactional
  public CartResponseDTO syncCartFromLocalStorage(UUID userId, List<CartItemRequestDTO> localCartItems) {
    Optional<Cart> existingCartOpt = cartRepository.findByUserIdAndStatusAndEnabled(userId, CartStatus.ACTIVE, true);

    Cart cart;
    if (existingCartOpt.isPresent()) {
      cart = existingCartOpt.get();
    } else {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new ValidationException("Usuario no encontrado"));
      cart = new Cart();
      cart.setUser(user);
      cart.setStatus(CartStatus.ACTIVE);
      cart.setEnabled(true);
    }

    for (CartItemRequestDTO localItem : localCartItems) {

      if (localItem.getQuantity() > 5) {
        throw new CartValidationException("No se pueden agregar más de 5 unidades del mismo producto");
      }

      Product product = productRepository.findById(localItem.getProductId())
          .orElseThrow(() -> new ValidationException("Producto no encontrado: " + localItem.getProductId()));

      Optional<CartItem> existingItemOpt = cart.getItems().stream()
          .filter(item -> item.getProduct().getId().equals(localItem.getProductId()))
          .findFirst();

      if (existingItemOpt.isPresent()) {
        CartItem existingItem = existingItemOpt.get();
        existingItem.setQuantity(localItem.getQuantity());
        existingItem.setSubtotal(calculateSubtotal(product, localItem.getQuantity()));
      } else {
        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setProduct(product);
        newItem.setQuantity(localItem.getQuantity());
        newItem.setUnitPrice(product.getPrice().getUnit());
        newItem.setSubtotal(calculateSubtotal(product, localItem.getQuantity()));
        cart.getItems().add(newItem);
      }
    }
    updateCartTotal(cart);
    cartRepository.save(cart);
    return cartMapper.toDto(cart);
  }

  @Transactional
  public CartResponseDTO createCart(UUID userId) {
    Optional<Cart> existingCart = cartRepository.findByUserIdAndStatusAndEnabled(userId, CartStatus.ACTIVE, true);
    if (existingCart.isPresent()) {
      return cartMapper.toDto(existingCart.get());
    }
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ValidationException("Usuario no encontrado"));

    Cart cart = new Cart();
    cart.setUser(user);
    cart.setStatus(CartStatus.ACTIVE);
    cart.setEnabled(true);

    return cartMapper.toDto(cartRepository.save(cart));
  }


  @Transactional
  public CartResponseDTO addItemToCart(UUID cartId, CartItemRequestDTO itemRequest) {
    Cart cart = cartRepository.findById(cartId)
        .orElseThrow(() -> new ValidationException("Carrito no encontrado"));

    Product product = productRepository.findById(itemRequest.getProductId())
        .orElseThrow(() -> new ValidationException("Producto no encontrado"));

    validateProductLimit(cart, itemRequest.getProductId(), itemRequest.getQuantity());

    Optional<CartItem> existingItem = cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(itemRequest.getProductId()))
        .findFirst();

    if (existingItem.isPresent()) {
      CartItem item = existingItem.get();
      item.setQuantity(item.getQuantity() + itemRequest.getQuantity());
      BigDecimal subtotal = calculateSubtotal(product, item.getQuantity());
      item.setSubtotal(subtotal);
      cartItemRepository.save(item);
    } else {
      CartItem newItem = new CartItem();
      newItem.setCart(cart);
      newItem.setProduct(product);
      newItem.setQuantity(itemRequest.getQuantity());
      newItem.setUnitPrice(product.getPrice().getUnit());
      BigDecimal subtotal = calculateSubtotal(product, itemRequest.getQuantity());
      newItem.setSubtotal(subtotal);
      cart.getItems().add(newItem);
    }

    updateCartTotal(cart);
    cartRepository.save(cart);

    return cartMapper.toDto(cart);
  }

  @Transactional
  public CartResponseDTO updateCartItemQuantity(UUID cartId, UUID itemId, int quantity) {
    System.out.println("Updating item quantity: " + itemId + " to " + quantity);
    Cart cart = cartRepository.findById(cartId)
        .orElseThrow(() -> new ValidationException("Carrito no encontrado"));

    CartItem item = cart.getItems().stream()
        .filter(i -> i.getId().equals(itemId))
        .findFirst()
        .orElseThrow(() -> new ValidationException("Item no encontrado en el carrito"));

    if (quantity <= 0) {
      cart.getItems().remove(item);
      cartItemRepository.delete(item);
    } else {
      if (quantity > 5) {
        throw new CartValidationException("No se pueden agregar más de 5 unidades del mismo producto");
      }

      item.setQuantity(quantity);
      BigDecimal subtotal = calculateSubtotal(item.getProduct(), quantity);
      item.setSubtotal(subtotal);
    }

    updateCartTotal(cart);
    cartRepository.save(cart);

    return cartMapper.toDto(cart);
  }

  @Transactional
  public void removeItemFromCart(UUID cartId, UUID itemId) {
    Cart cart = cartRepository.findById(cartId)
        .orElseThrow(() -> new ValidationException("Carrito no encontrado"));

    cart.getItems().removeIf(item -> {
      if (item.getId().equals(itemId)) {
        cartItemRepository.delete(item);
        return true;
      }
      return false;
    });

    updateCartTotal(cart);
    cartRepository.save(cart);
  }

  @Transactional
  public void clearCart(UUID cartId) {
    Cart cart = cartRepository.findById(cartId)
        .orElseThrow(() -> new ValidationException("Carrito no encontrado"));

    cartItemRepository.deleteAll(cart.getItems());
    cart.getItems().clear();
    cart.setTotalPrice(BigDecimal.ZERO);

    cartRepository.save(cart);
  }

  private void addItemsToCart(Cart cart, List<CartItemRequestDTO> items) {
    for (CartItemRequestDTO itemRequest : items) {
      Product product = productRepository.findById(itemRequest.getProductId())
          .orElseThrow(() -> new ValidationException("Producto no encontrado: " + itemRequest.getProductId()));

      CartItem item = new CartItem();
      item.setCart(cart);
      item.setProduct(product);
      item.setQuantity(itemRequest.getQuantity());
      item.setUnitPrice(product.getPrice().getUnit());

      BigDecimal subtotal = calculateSubtotal(product, itemRequest.getQuantity());
      item.setSubtotal(subtotal);

      cart.getItems().add(item);
    }

    updateCartTotal(cart);
  }

  private void updateCartTotal(Cart cart) {
    BigDecimal total = cart.getItems().stream()
        .map(CartItem::getSubtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    cart.setTotalPrice(total);
  }


  public BigDecimal calculateSubtotal(Product product, int quantity) {
    Price pricing = product.getPrice();
    if (pricing == null) {
      throw new ValidationException("Product price not found");
    }
    if (quantity == 1) return pricing.getUnit();
    if (quantity == 2) return pricing.getTwoUnits() != null ?
        pricing.getTwoUnits() :
        pricing.getUnit().multiply(BigDecimal.valueOf(2));
    if (quantity == 3) return pricing.getThreeUnits() != null ?
        pricing.getThreeUnits() :
        pricing.getUnit().multiply(BigDecimal.valueOf(3));
    if (quantity == 4)
      return pricing.getThreeUnits() != null ?
          pricing.getThreeUnits().divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(4)) :
          pricing.getUnit().multiply(BigDecimal.valueOf(4));
    else
      return calculateComplexPrice(product, quantity);
  }

  private BigDecimal calculateComplexPrice(Product product, int quantity) {
    BigDecimal subtotal;
    int promotionSets = quantity / 5;
    int remainder = quantity % 5;
    subtotal = (product.getPrice().getThreeUnits() != null ?
        product.getPrice().getThreeUnits().multiply(BigDecimal.valueOf(3)) :
        product.getPrice().getUnit().multiply(BigDecimal.valueOf(3))).multiply(BigDecimal.valueOf(promotionSets));
    if (promotionSets == 0) {
      return subtotal;
    }
  return subtotal.add(calculateSubtotal(product, remainder));
  }

  private void validateProductLimit(Cart cart, UUID productId, int newQuantity) {
    Optional<CartItem> existingItem = cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(productId))
        .findFirst();

    int currentQuantity = existingItem.map(CartItem::getQuantity).orElse(0);
    int totalQuantity = currentQuantity + newQuantity;

    if (totalQuantity > 5) {
      throw new CartValidationException("No se pueden agregar más de 5 unidades del mismo producto");
    }
  }
}
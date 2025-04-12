package com.natuvida.store.service;

import com.natuvida.store.dto.request.CartItemRequestDTO;
import com.natuvida.store.dto.request.CartRequestDTO;
import com.natuvida.store.dto.response.CartResponseDTO;
import com.natuvida.store.entity.*;
import com.natuvida.store.enums.CartStatus;
import com.natuvida.store.exception.ValidationException;
import com.natuvida.store.mapper.CartMapper;
import com.natuvida.store.repository.CartItemRepository;
import com.natuvida.store.repository.CartRepository;
import com.natuvida.store.repository.ProductRepository;
import com.natuvida.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
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
    Cart cart;
    if (userId != null) {
      Optional<Cart> userCart = cartRepository.findByUserIdAndStatusAndEnabled(userId, CartStatus.ACTIVE, true);
      if (userCart.isPresent()) {
        return cartMapper.toDto(userCart.get());
      }
    }
    return null;
  }

  @Transactional
  public CartResponseDTO createCart(CartRequestDTO cartRequestDTO) {
    Cart cart = new Cart();

    if (cartRequestDTO.getUserId() != null) {
      User user = userRepository.findById(cartRequestDTO.getUserId())
          .orElseThrow(() -> new ValidationException("Usuario no encontrado"));
      cart.setUser(user);
    } else {
      throw new ValidationException("Se debe proporcionar un userId o sessionId");
    }
    cart.setStatus(CartStatus.ACTIVE);
    cart.setEnabled(true);

    Cart savedCart = cartRepository.save(cart);

    if (cartRequestDTO.getItems() != null && !cartRequestDTO.getItems().isEmpty()) {
      addItemsToCart(savedCart, cartRequestDTO.getItems());
    }

    return cartMapper.toDto(savedCart);
  }

  @Transactional
  public CartResponseDTO addItemToCart(UUID cartId, CartItemRequestDTO itemRequest) {
    Cart cart = cartRepository.findById(cartId)
        .orElseThrow(() -> new ValidationException("Carrito no encontrado"));

    Product product = productRepository.findById(itemRequest.getProductId())
        .orElseThrow(() -> new ValidationException("Producto no encontrado"));

    // Verificar si el producto ya está en el carrito
    Optional<CartItem> existingItem = cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(itemRequest.getProductId()))
        .findFirst();

    if (existingItem.isPresent()) {
      // Actualiza la cantidad
      CartItem item = existingItem.get();
      item.setQuantity(item.getQuantity() + itemRequest.getQuantity());
      BigDecimal subtotal = calculateSubtotal(product, item.getQuantity());
      item.setSubtotal(subtotal);
      cartItemRepository.save(item);
    } else {
      // Crea un nuevo item
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
    Cart cart = cartRepository.findById(cartId)
        .orElseThrow(() -> new ValidationException("Carrito no encontrado"));

    CartItem item = cart.getItems().stream()
        .filter(i -> i.getId().equals(itemId))
        .findFirst()
        .orElseThrow(() -> new ValidationException("Item no encontrado en el carrito"));

    if (quantity <= 0) {
      // Eliminar el item si la cantidad es 0 o negativa
      cart.getItems().remove(item);
      cartItemRepository.delete(item);
    } else {
      // Actualizar la cantidad
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

    BigDecimal subtotal;
    switch (quantity) {
      case 1 -> subtotal = pricing.getUnit();
      case 2 -> subtotal = pricing.getTwoUnits() != null ?
          pricing.getTwoUnits() : pricing.getUnit().multiply(BigDecimal.valueOf(2));
      case 3 -> subtotal = pricing.getThreeUnits() != null ?
          pricing.getThreeUnits() : pricing.getUnit().multiply(BigDecimal.valueOf(3));
      default -> {
        // For quantities > 3, calculate based on promotions if available
        int sets = quantity / 3;
        int remainder = quantity % 3;
        subtotal = calculateMultiSetPrice(pricing, sets, remainder);
      }
    }
    return subtotal;
  }

  // TODO: Validar lógica de precios
  private BigDecimal calculateMultiSetPrice(Price pricing, int sets, int remainder) {
    BigDecimal basePrice = pricing.getThreeUnits() != null ?
        pricing.getThreeUnits().multiply(BigDecimal.valueOf(sets)) :
        pricing.getUnit().multiply(BigDecimal.valueOf(sets * 3));

    if (remainder == 0) return basePrice;
    if (remainder == 1) return basePrice.add(pricing.getUnit());
    return basePrice.add(pricing.getTwoUnits() != null ?
        pricing.getTwoUnits() : pricing.getUnit().multiply(BigDecimal.valueOf(2)));
  }
}
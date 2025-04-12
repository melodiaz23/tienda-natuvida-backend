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
  public CartResponseDTO syncCartFromLocalStorage(UUID userId, List<CartItemRequestDTO> localCartItems) {
    // Buscar si el usuario ya tiene un carrito activo
    Optional<Cart> existingCartOpt = cartRepository.findByUserIdAndStatusAndEnabled(userId, CartStatus.ACTIVE, true);

    Cart cart;
    if (existingCartOpt.isPresent()) {
      cart = existingCartOpt.get();
    } else {
      // Crear un nuevo carrito para el usuario
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new ValidationException("Usuario no encontrado"));
      cart = new Cart();
      cart.setUser(user);
      cart.setStatus(CartStatus.ACTIVE);
      cart.setEnabled(true);
    }

    // Fusionar los productos del localStorage con el carrito existente
    for (CartItemRequestDTO localItem : localCartItems) {
      Product product = productRepository.findById(localItem.getProductId())
          .orElseThrow(() -> new ValidationException("Producto no encontrado: " + localItem.getProductId()));

      // Verificar si el producto ya está en el carrito
      Optional<CartItem> existingItemOpt = cart.getItems().stream()
          .filter(item -> item.getProduct().getId().equals(localItem.getProductId()))
          .findFirst();

      if (existingItemOpt.isPresent()) {
        // Actualizar la cantidad del item existente
        CartItem existingItem = existingItemOpt.get();
        existingItem.setQuantity(localItem.getQuantity());
        existingItem.setSubtotal(calculateSubtotal(product, localItem.getQuantity()));
      } else {
        // Agregar nuevo item
        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setProduct(product);
        newItem.setQuantity(localItem.getQuantity());
        newItem.setUnitPrice(product.getPrice().getUnit());
        newItem.setSubtotal(calculateSubtotal(product, localItem.getQuantity()));
        cart.getItems().add(newItem);
      }
    }
    // Actualizar el total del carrito
    updateCartTotal(cart);
    cartRepository.save(cart);
    return cartMapper.toDto(cart);
  }

  @Transactional
  public CartResponseDTO createCart(UUID userId) {
    // Verificar si el usuario ya tiene un carrito activo
    Optional<Cart> existingCart = cartRepository.findByUserIdAndStatusAndEnabled(userId, CartStatus.ACTIVE, true);
    if (existingCart.isPresent()) {
      return cartMapper.toDto(existingCart.get());
    }
    // Crear un nuevo carrito para el usuario
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
    // Casos base para cantidades pequeñas
    if (quantity == 1) return pricing.getUnit();
    if (quantity == 2) return pricing.getTwoUnits() != null ?
        pricing.getTwoUnits() :
        pricing.getUnit().multiply(BigDecimal.valueOf(2));
    if (quantity == 3) return pricing.getThreeUnits() != null ?
        pricing.getThreeUnits() :
        pricing.getUnit().multiply(BigDecimal.valueOf(3));
    // Para cantidades mayores, aplicamos la lógica de promociones
    // TODO: Validar lógica de promociones
    return calculateComplexPrice(pricing, quantity);
  }

  private BigDecimal calculateComplexPrice(Price pricing, int quantity) {
    BigDecimal total = BigDecimal.ZERO;

    // Promoción: pague 3 lleve 5
    if (pricing.getFiveByThree() != null && quantity >= 5) {
      int promotionSets = quantity / 5;  // Cuántos sets completos de 5
      int remainder = quantity % 5;      // Unidades restantes

      // Aplicar el precio de la promoción por cada set completo de 5
      total = total.add(pricing.getFiveByThree().multiply(BigDecimal.valueOf(promotionSets)));

      // Si quedan unidades, calcular su precio
      if (remainder > 0) {
        if (remainder == 1) {
          total = total.add(pricing.getUnit());
        } else if (remainder == 2) {
          total = total.add(pricing.getTwoUnits() != null ?
              pricing.getTwoUnits() :
              pricing.getUnit().multiply(BigDecimal.valueOf(2)));
        } else if (remainder == 3) {
          total = total.add(pricing.getThreeUnits() != null ?
              pricing.getThreeUnits() :
              pricing.getUnit().multiply(BigDecimal.valueOf(3)));
        } else { // remainder == 4
          // Para 4 unidades restantes, usamos el precio de 3 + precio de 1
          BigDecimal price3 = pricing.getThreeUnits() != null ?
              pricing.getThreeUnits() :
              pricing.getUnit().multiply(BigDecimal.valueOf(3));
          total = total.add(price3).add(pricing.getUnit());
        }
      }
    }
    // Si no hay promoción de 5x3 o la cantidad es menor que 5
    else {
      // Calcular usando múltiplos de 3 y el resto
      int sets = quantity / 3;
      int remainder = quantity % 3;

      // Precio para los sets completos de 3
      BigDecimal price3 = pricing.getThreeUnits() != null ?
          pricing.getThreeUnits() :
          pricing.getUnit().multiply(BigDecimal.valueOf(3));
      total = total.add(price3.multiply(BigDecimal.valueOf(sets)));

      // Agregar precio por los items adicionales
      if (remainder == 1) {
        total = total.add(pricing.getUnit());
      } else if (remainder == 2) {
        total = total.add(pricing.getTwoUnits() != null ?
            pricing.getTwoUnits() :
            pricing.getUnit().multiply(BigDecimal.valueOf(2)));
      }
    }

    return total;
  }
}
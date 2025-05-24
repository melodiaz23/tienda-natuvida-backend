package com.natuvida.store.service;

import com.natuvida.store.dto.request.OrderItemRequestDTO;
import com.natuvida.store.dto.request.OrderRequestDTO;
import com.natuvida.store.dto.response.OrderResponseDTO;
import com.natuvida.store.dto.response.UserResponseDTO;
import com.natuvida.store.entity.Customer;
import com.natuvida.store.entity.Order;
import com.natuvida.store.entity.OrderItem;
import com.natuvida.store.entity.Product;
import com.natuvida.store.enums.OrderStatus;
import com.natuvida.store.exception.ValidationException;
import com.natuvida.store.mapper.OrderMapper;
import com.natuvida.store.repository.CustomerRepository;
import com.natuvida.store.repository.OrderRepository;
import com.natuvida.store.repository.ProductRepository;
import com.natuvida.store.util.PriceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final ProductRepository productRepository;
  private final UserService userService;
  private final OrderMapper orderMapper;
  private final PriceCalculator priceCalculator;

  @Transactional(readOnly = true)
  public List<OrderResponseDTO> getAllOrders() {
    return orderMapper.toDtoList(orderRepository.findAll());
  }

  @Transactional(readOnly = true)
  public OrderResponseDTO getOrderById(UUID id) {
    Order order = orderRepository.findById(id)
        .orElseThrow(() -> new ValidationException("Orden no encontrada"));
    return orderMapper.toDto(order);
  }

  @Transactional(readOnly = true)
  public List<OrderResponseDTO> getOrdersByCustomerId(UUID customerId) {
    List<Order> orders = orderRepository.findByCustomerId(customerId);
    return orderMapper.toDtoList(orders);
  }

  public List<OrderResponseDTO> getOrdersByUserEmail(String email) {
    // Buscar el usuario por email
    UserResponseDTO user = userService.findByEmail(email);
    if (user == null) {
      throw new NoSuchElementException("Usuario no encontrado");
    }
    // Buscar el customer asociado a ese usuario
    Optional<Customer> customer = customerRepository.findByUserId(user.getId());
    // Usuario sin customer (no ha comprado)
    return customer.map(value -> orderRepository.findByCustomerId(value.getId())
        .stream()
        .map(orderMapper::toDto)
        .collect(Collectors.toList())).orElse(Collections.emptyList());
  }

  @Transactional
  public OrderResponseDTO createOrder(OrderRequestDTO orderRequest) {
    Customer customer = customerRepository.findById(orderRequest.getCustomerId())
        .orElseThrow(() -> new ValidationException("Cliente no encontrado"));
    Order order = new Order(
        generateOrderNumber(),
        LocalDateTime.now(),
        orderRequest.getShippingAddress()
    );

    order.setCustomer(customer);
    order.setPaymentMethod(orderRequest.getPaymentMethod());
    order.setNotes(orderRequest.getNotes());
    order.setStatus(OrderStatus.PENDING);

    // Crear y agregar items
    List<OrderItem> orderItems = createOrderItems(order, orderRequest.getItems());
    orderItems.forEach((orderItem) -> {
      order.getOrderItems().add(orderItem);
    });

    // Calcular total
    BigDecimal total = orderItems.stream()
        .map(OrderItem::getSubtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    order.setTotalAmount(total);
    Order savedOrder = orderRepository.save(order);
    return orderMapper.toDto(savedOrder);
  }

  @Transactional
  public OrderResponseDTO updateOrderStatus(UUID orderId, OrderStatus newStatus) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ValidationException("Orden no encontrada"));
    order.setStatus(newStatus);

    return orderMapper.toDto(orderRepository.save(order));
  }

  @Transactional
  public void deleteOrder(UUID orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new ValidationException("Orden no encontrada"));
    // Soft delete
    order.setEnabled(false);
    orderRepository.save(order);
  }

  private List<OrderItem> createOrderItems(Order order, List<OrderItemRequestDTO> itemsRequest) {
    return itemsRequest.stream().map(itemRequest -> {
      Product product = productRepository.findById(itemRequest.getProductId())
          .orElseThrow(() -> new ValidationException("Producto no encontrado: " + itemRequest.getProductId()));

      PriceCalculator.PriceResult priceResult = priceCalculator.calculateItemPrices(
          product.getPrice(), itemRequest.getQuantity());

      OrderItem orderItem = new OrderItem(product, itemRequest.getQuantity());
      orderItem.setOrder(order);
      orderItem.setUnitPrice(priceResult.getUnitPrice());
      orderItem.setProductName(product.getName());
      orderItem.setSubtotal(priceResult.getSubtotal());
      return orderItem;
    }).collect(Collectors.toList());
  }

  private String generateOrderNumber() {
    return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
  }

}
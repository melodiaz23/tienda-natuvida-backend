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
import com.natuvida.store.exception.order.OrderValidationException;
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
    System.out.println("Creando orden: " + orderRequest);
    Customer customer = null;

    if (orderRequest.getCustomer().getNationalId() != null) {
      Optional<Customer> existingCustomerById = customerRepository.findByNationalId(
          orderRequest.getCustomer().getNationalId());
      if (existingCustomerById.isPresent()) {
        customer = existingCustomerById.get();
      }
    }

    if (customer == null){
    Optional<Customer> existingCustomerByPhone = customerRepository.findByPhoneNumber(
        orderRequest.getCustomer().getPhoneNumber());
    if (existingCustomerByPhone.isPresent()) {
      customer = existingCustomerByPhone.get();
    }
    }

    if (customer == null && orderRequest.getCustomer() != null) {
      customer = new Customer(
          orderRequest.getCustomer().getFirstName(),
          orderRequest.getCustomer().getLastName(),
          orderRequest.getCustomer().getPhoneNumber(),
          orderRequest.getCustomer().getAddress(),
          orderRequest.getCustomer().getCity()
      );

      if (orderRequest.getCustomer().getNationalId() != null) {
        customer.setNationalId(orderRequest.getCustomer().getNationalId());
      }

      customerRepository.save(customer);
    } else if (customer == null) {
      throw new ValidationException("Debe proporcionar un ID de cliente existente o datos para crear uno nuevo");
    }

    System.out.println(customer);

    Order order = new Order(
        generateOrderNumber(),
        LocalDateTime.now(),
        orderRequest.getShippingAddress()
    );

    System.out.println("New order: " + order);

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
    System.out.println("Orden guardada: " + savedOrder.getId());
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

      if (itemRequest.getQuantity() > 5) {
        throw new OrderValidationException("No se pueden ordenar más de 5 unidades del producto: " + product.getName());
      }

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

  @Transactional
  private String generateOrderNumber() {
    Integer maxNumber = orderRepository.getMaxOrderNumber();
    return String.format("NV-%05d", maxNumber + 1);
  }

}
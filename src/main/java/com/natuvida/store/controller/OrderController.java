package com.natuvida.store.controller;

import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.dto.request.OrderRequestDTO;
import com.natuvida.store.dto.response.OrderResponseDTO;
import com.natuvida.store.enums.OrderStatus;
import com.natuvida.store.service.OrderService;
import com.natuvida.store.service.UserService;
import com.natuvida.store.util.ApiPaths;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.ORDERS)
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final UserService userService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getAllOrders() {
    List<OrderResponseDTO> orders = orderService.getAllOrders();
    return ResponseEntity.ok(ApiResponse.success(orders, "Consulta exitosa"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderById(@PathVariable UUID id) {
    OrderResponseDTO order = orderService.getOrderById(id);
    return ResponseEntity.ok(ApiResponse.success(order, "Consulta exitosa"));
  }

  @GetMapping("/customer/{customerId}")
  public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrdersByCustomer(@PathVariable UUID customerId) {
    List<OrderResponseDTO> orders = orderService.getOrdersByCustomerId(customerId);
    return ResponseEntity.ok(ApiResponse.success(orders, "Consulta exitosa"));
  }

  @GetMapping("/my-orders")
  public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getMyOrders() {
    // Get authenticated user email directly
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error("Debe iniciar sesión para ver sus órdenes"));
    }

    // Get orders in a single service call that handles the entire operation
    List<OrderResponseDTO> orders = orderService.getOrdersByUserEmail(auth.getName());
    return ResponseEntity.ok(ApiResponse.success(orders, "Consulta exitosa"));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(@Valid @RequestBody OrderRequestDTO orderRequest) {
    OrderResponseDTO createdOrder = orderService.createOrder(orderRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(createdOrder, "Orden creada exitosamente"));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<ApiResponse<OrderResponseDTO>> updateOrderStatus(
      @PathVariable UUID id,
      @RequestParam OrderStatus status) {
    OrderResponseDTO updatedOrder = orderService.updateOrderStatus(id, status);
    return ResponseEntity.ok(ApiResponse.success(updatedOrder, "Estado de orden actualizado"));
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable UUID id) {
    orderService.deleteOrder(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Orden eliminada exitosamente"));
  }
}
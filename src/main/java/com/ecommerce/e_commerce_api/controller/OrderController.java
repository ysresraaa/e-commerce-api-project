package com.ecommerce.e_commerce_api.controller;

import com.ecommerce.e_commerce_api.dto.OrderResponseDTO;
import com.ecommerce.e_commerce_api.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDTO> placeOrderForAuthenticatedUser() {
        OrderResponseDTO order = orderService.placeOrderForCurrentUser();
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrdersForAuthenticatedCustomer() {

        List<OrderResponseDTO> orders = orderService.getAllOrdersForCurrentUser();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER') and @orderService.isOwnerOfOrder(#orderId, authentication.principal.id)")
    public ResponseEntity<OrderResponseDTO> getOrderByIdForAuthenticatedUser(@PathVariable Long orderId) {

        OrderResponseDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/code/{orderCode}")
    @PreAuthorize("hasRole('USER') and @orderService.isOwnerOfOrderCode(#orderCode, authentication.principal.id)")
    public ResponseEntity<OrderResponseDTO> getOrderByCodeForAuthenticatedUser(@PathVariable String orderCode) {

        OrderResponseDTO order = orderService.getOrderByOrderCode(orderCode);
        return ResponseEntity.ok(order);
    }
}
// OrderController.java
package com.ecommerce.e_commerce_api.controller;

import com.ecommerce.e_commerce_api.dto.OrderResponseDTO;
import com.ecommerce.e_commerce_api.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ecommerce.e_commerce_api.model.Customer;


import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    private Long getAuthenticatedCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new AccessDeniedException("User not authenticated.");
        }
        Customer authenticatedCustomer = (Customer) authentication.getPrincipal();
        return authenticatedCustomer.getId();
    }

    @PostMapping("/{customerId}")
    public ResponseEntity<OrderResponseDTO> placeOrder(@PathVariable Long customerId) {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        if (!authenticatedCustomerId.equals(customerId)) {
            throw new AccessDeniedException("You are not authorized to place an order for this customer.");
        }
        OrderResponseDTO order = orderService.placeOrder(customerId);
        return ResponseEntity.ok(order);
    }


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrdersForCustomer(@PathVariable Long customerId) {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        if (!authenticatedCustomerId.equals(customerId)) {
            throw new AccessDeniedException("You are not authorized to view orders for this customer.");
        }
        List<OrderResponseDTO> orders = orderService.getAllOrdersForCustomer(customerId);
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long orderId) {
        // Retrieve the order first to check its customerId
        OrderResponseDTO order = orderService.getOrderById(orderId);

        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        if (!authenticatedCustomerId.equals(order.getCustomerId())) { // Check if the order belongs to the authenticated user
            throw new AccessDeniedException("You are not authorized to view this order.");
        }

        return ResponseEntity.ok(order);
    }


    @GetMapping("/code/{orderCode}")
    public ResponseEntity<OrderResponseDTO> getOrderByOrderCode(@PathVariable String orderCode) {
        OrderResponseDTO order = orderService.getOrderByOrderCode(orderCode);

        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        if (!authenticatedCustomerId.equals(order.getCustomerId())) {
            throw new AccessDeniedException("You are not authorized to view this order with this code.");
        }
        return ResponseEntity.ok(order);
    }
}
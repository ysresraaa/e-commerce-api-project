
package com.ecommerce.e_commerce_api.controller;

import com.ecommerce.e_commerce_api.dto.OrderResponseDTO;
import com.ecommerce.e_commerce_api.service.OrderService;
import com.ecommerce.e_commerce_api.model.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof Customer)) {

            throw new org.springframework.security.access.AccessDeniedException("User not authenticated or principal is not of expected type Customer.");
        }
        Customer authenticatedCustomer = (Customer) authentication.getPrincipal();
        return authenticatedCustomer.getId();
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> placeOrderForAuthenticatedUser() {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        OrderResponseDTO order = orderService.placeOrder(authenticatedCustomerId);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrdersForAuthenticatedCustomer() {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        List<OrderResponseDTO> orders = orderService.getAllOrdersForCustomer(authenticatedCustomerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderByIdForAuthenticatedUser(@PathVariable Long orderId) {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();

        OrderResponseDTO order = orderService.getOrderByIdAndCustomerId(orderId, authenticatedCustomerId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/code/{orderCode}")
    public ResponseEntity<OrderResponseDTO> getOrderByCodeForAuthenticatedUser(@PathVariable String orderCode) {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        OrderResponseDTO order = orderService.getOrderByOrderCodeAndCustomerId(orderCode, authenticatedCustomerId);
        return ResponseEntity.ok(order);
    }
}
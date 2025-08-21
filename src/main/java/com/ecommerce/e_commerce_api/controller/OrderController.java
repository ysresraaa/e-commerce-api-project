package com.ecommerce.e_commerce_api.controller;

import com.ecommerce.e_commerce_api.dto.OrderResponseDTO;
import com.ecommerce.e_commerce_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDTO> placeOrderForAuthenticatedUser() {
        OrderResponseDTO order = orderService.placeOrderForCurrentUser();
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersForAuthenticatedUser(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrderResponseDTO> orders = orderService.getOrdersForCurrentUser(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{orderCode}")
    @PreAuthorize("hasRole('ADMIN') or @orderService.isOwnerOfOrder(authentication, #orderCode)")
    public ResponseEntity<OrderResponseDTO> getOrderByCode(@PathVariable UUID orderCode) {
        OrderResponseDTO order = orderService.getOrderByOrderCode(orderCode);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/customers/{customerCode}/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByCustomer(
            @PathVariable UUID customerCode,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrderResponseDTO> orders = orderService.getOrdersByCustomerCode(customerCode, pageable);
        return ResponseEntity.ok(orders);
    }
}
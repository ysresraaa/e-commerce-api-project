// CartController.java
package com.ecommerce.e_commerce_api.controller;

import com.ecommerce.e_commerce_api.dto.AddToCartRequestDTO;
import com.ecommerce.e_commerce_api.model.Cart;
import com.ecommerce.e_commerce_api.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ecommerce.e_commerce_api.model.Customer;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    private Long getAuthenticatedCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new AccessDeniedException("User not authenticated.");
        }

        Customer authenticatedCustomer = (Customer) authentication.getPrincipal();
        return authenticatedCustomer.getId();
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Cart> getCartByCustomerId(@PathVariable Long customerId) {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        if (!authenticatedCustomerId.equals(customerId)) {
            throw new AccessDeniedException("You are not authorized to view this cart.");
        }
        Cart cart = cartService.getCartByCustomerId(customerId);
        return ResponseEntity.ok(cart);
    }


    @PostMapping("/{customerId}/add")
    public ResponseEntity<Cart> addProductToCart(@PathVariable Long customerId, @RequestBody AddToCartRequestDTO request) {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        if (!authenticatedCustomerId.equals(customerId)) {
            throw new AccessDeniedException("You are not authorized to modify this cart.");
        }
        Cart updatedCart = cartService.addProductToCart(customerId, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }


    @PostMapping("/{customerId}/remove/{productId}")
    public ResponseEntity<Cart> removeProductFromCart(@PathVariable Long customerId, @PathVariable Long productId) {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        if (!authenticatedCustomerId.equals(customerId)) {
            throw new AccessDeniedException("You are not authorized to modify this cart.");
        }
        Cart updatedCart = cartService.removeProductFromCart(customerId, productId);
        return ResponseEntity.ok(updatedCart);
    }


    @PostMapping("/{customerId}/clear")
    public ResponseEntity<Cart> clearCart(@PathVariable Long customerId) {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        if (!authenticatedCustomerId.equals(customerId)) {
            throw new AccessDeniedException("You are not authorized to clear this cart.");
        }
        Cart clearedCart = cartService.clearCart(customerId);
        return ResponseEntity.ok(clearedCart);
    }
}
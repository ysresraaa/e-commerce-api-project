

package com.ecommerce.e_commerce_api.controller;

import com.ecommerce.e_commerce_api.dto.AddToCartRequestDTO;
import com.ecommerce.e_commerce_api.service.CartService;
import com.ecommerce.e_commerce_api.model.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.e_commerce_api.dto.CartResponseDTO;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private Long getAuthenticatedCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof Customer)) {
            throw new org.springframework.security.access.AccessDeniedException("User not authenticated or principal is not of expected type Customer.");
        }
        Customer authenticatedCustomer = (Customer) authentication.getPrincipal();
        return authenticatedCustomer.getId();
    }

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCartOfAuthenticatedUser() {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        CartResponseDTO cart = cartService.getCartByCustomerId(authenticatedCustomerId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addProductToCart(@RequestBody AddToCartRequestDTO request) {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        CartResponseDTO updatedCart = cartService.addProductToCart(authenticatedCustomerId, request.getProductId(), request.getQuantity()); // Servis metodu bu ID'yi kullanacak
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    @PostMapping("/remove/{productId}")
    public ResponseEntity<CartResponseDTO> removeProductFromCart(@PathVariable Long productId) {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        CartResponseDTO updatedCart = cartService.removeProductFromCart(authenticatedCustomerId, productId);
        return ResponseEntity.ok(updatedCart);
    }

    @PostMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        cartService.clearCart(authenticatedCustomerId);
        return ResponseEntity.noContent().build();
    }
}
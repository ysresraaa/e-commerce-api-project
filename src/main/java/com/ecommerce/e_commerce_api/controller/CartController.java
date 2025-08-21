package com.ecommerce.e_commerce_api.controller;

import com.ecommerce.e_commerce_api.dto.AddToCartRequest;
import com.ecommerce.e_commerce_api.dto.CartResponseDTO;
import com.ecommerce.e_commerce_api.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponseDTO> getCartOfAuthenticatedUser() {
        CartResponseDTO cart = cartService.getCartForCurrentUser();
        return ResponseEntity.ok(cart);
    }


    @PostMapping("/items/{productCode}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponseDTO> addProductToCart(
            @PathVariable UUID productCode,
            @Valid @RequestBody AddToCartRequest request) {

        CartResponseDTO updatedCart = cartService.addProductToCart(productCode, request.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }


    @DeleteMapping("/items/{productCode}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponseDTO> removeProductFromCart(@PathVariable UUID productCode) {
        CartResponseDTO updatedCart = cartService.removeProductFromCart(productCode);
        return ResponseEntity.ok(updatedCart);
    }


    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCartForCurrentUser();
        return ResponseEntity.noContent().build();
    }
}
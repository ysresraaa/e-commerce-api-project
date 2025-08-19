

package com.ecommerce.e_commerce_api.controller;

import com.ecommerce.e_commerce_api.dto.AddToCartRequestDTO;
import com.ecommerce.e_commerce_api.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.e_commerce_api.dto.CartResponseDTO;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {

        this.cartService = cartService;
    }


    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponseDTO> getCartOfAuthenticatedUser() {

        CartResponseDTO cart = cartService.getCartForCurrentUser();
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponseDTO> addProductToCart(@RequestBody AddToCartRequestDTO request) {
        CartResponseDTO updatedCart = cartService.addProductToCart(request.getProductId(),request.getQuantity());
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    @PostMapping("/remove/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponseDTO> removeProductFromCart(@PathVariable Long productId) {

        CartResponseDTO updatedCart = cartService.removeProductFromCart(productId);
        return ResponseEntity.ok(updatedCart);
    }

    @PostMapping("/clear")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> clearCart() {
         cartService.clearCartForCurrentUser();
        return ResponseEntity.noContent().build();
    }
}
package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.dto.CartResponseDTO;
import com.ecommerce.e_commerce_api.exception.InsufficientStockException;
import com.ecommerce.e_commerce_api.exception.ResourceNotFoundException;
import com.ecommerce.e_commerce_api.mapper.CartMapper;
import com.ecommerce.e_commerce_api.model.Cart;
import com.ecommerce.e_commerce_api.model.CartItem;
import com.ecommerce.e_commerce_api.model.Customer;
import com.ecommerce.e_commerce_api.model.Product;
import com.ecommerce.e_commerce_api.repository.ICartItemRepository;
import com.ecommerce.e_commerce_api.repository.ICartRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartService {

    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;
    private final ProductService productService;
    private final CartMapper cartMapper;
    private final CustomerService customerService;

    public CartService(ICartRepository cartRepository, ICartItemRepository cartItemRepository, ProductService productService, CustomerService customerService, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.customerService = customerService;
        this.cartMapper = cartMapper;
    }

    @Transactional(readOnly = true)
    public CartResponseDTO getCartForCurrentUser() {
        Cart cart = getCartModelForCurrentUser();
        return cartMapper.toDto(cart);
    }

    @Transactional
    public CartResponseDTO addProductToCart(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        Cart cart = getCartModelForCurrentUser();
        Product product = productService.findProductEntityById(productId);

        Optional<CartItem> existingItemOptional = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOptional.isPresent()) {
            CartItem cartItem = existingItemOptional.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            if (newQuantity > product.getStock()) {
                throw new InsufficientStockException("Not enough stock for product: " + product.getName());
            }
            cartItem.setQuantity(newQuantity);
        } else {
            if (quantity > product.getStock()) {
                throw new InsufficientStockException("Not enough stock for product: " + product.getName());
            }
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getPrice());
            cart.getItems().add(cartItem);
        }

        recalculateCartTotalPrice(cart);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Transactional
    public CartResponseDTO removeProductFromCart(Long productId) {
        Cart cart = getCartModelForCurrentUser();
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart with ID: " + productId));

        cart.getItems().remove(itemToRemove);
        cartItemRepository.delete(itemToRemove);

        recalculateCartTotalPrice(cart);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Transactional
    public void clearCartForCurrentUser() {
        Cart cart = getCartModelForCurrentUser();
        cartItemRepository.deleteAllInBatch(cart.getItems());
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public Cart getCartModelByCustomerId(Long customerId) {
        Customer customer = customerService.findCustomerEntityById(customerId);
        return cartRepository.findByCustomer(customer)
                .orElseGet(() -> createCartForCustomer(customer));
    }

    private Customer getAuthenticatedCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof Customer)) {
            throw new org.springframework.security.access.AccessDeniedException("User not authenticated or principal is not of expected type Customer.");
        }
        return (Customer) authentication.getPrincipal();
    }

    private Cart getCartModelForCurrentUser() {
        Customer customer = getAuthenticatedCustomer();
        return cartRepository.findByCustomer(customer)
                .orElseGet(() -> createCartForCustomer(customer));
    }

    private Cart createCartForCustomer(Customer customer) {
        Cart newCart = new Cart();
        newCart.setCustomer(customer);
        newCart.setTotalPrice(BigDecimal.ZERO);
        return cartRepository.save(newCart);
    }

    private void recalculateCartTotalPrice(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
    }
}
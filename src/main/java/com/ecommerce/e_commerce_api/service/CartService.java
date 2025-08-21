package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.dto.CartResponseDTO;
import com.ecommerce.e_commerce_api.exception.InsufficientStockException;
import com.ecommerce.e_commerce_api.exception.ResourceNotFoundException;
import com.ecommerce.e_commerce_api.mapper.CartMapper;
import com.ecommerce.e_commerce_api.model.Cart;
import com.ecommerce.e_commerce_api.model.CartItem;
import com.ecommerce.e_commerce_api.model.Customer;
import com.ecommerce.e_commerce_api.model.Product;
import com.ecommerce.e_commerce_api.repository.CartItemRepository;
import com.ecommerce.e_commerce_api.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final CustomerService customerService;
    private final CartMapper cartMapper;
    private final AuthenticationService authenticationService;

    @Transactional(readOnly = true)
    public CartResponseDTO getCartForCurrentUser() {
        Cart cart = getCartModelForCurrentUser();
        return cartMapper.toDto(cart);
    }

    @Transactional
    public CartResponseDTO addProductToCart(UUID productCode, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        Cart cart = getCartModelForCurrentUser();
        Product product = productService.findProductEntityByCode(productCode);

        Optional<CartItem> existingItemOptional = cart.getItems().stream()
                .filter(item -> item.getProduct().getProductCode().equals(productCode))
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
    public CartResponseDTO removeProductFromCart(UUID productCode) {
        Cart cart = getCartModelForCurrentUser();
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().getProductCode().equals(productCode))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart with code: " + productCode));

        cart.getItems().remove(itemToRemove);
        cartItemRepository.delete(itemToRemove);

        recalculateCartTotalPrice(cart);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    @Transactional
    public void clearCartForCurrentUser() {
        Cart cart = getCartModelForCurrentUser();
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            cartItemRepository.deleteAllInBatch(cart.getItems());
            cart.getItems().clear();
        }
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public Cart getCartModelByCustomerCode(UUID customerCode) {
        Customer customer = customerService.findCustomerEntityByCode(customerCode);
        return cartRepository.findByCustomer(customer)
                .orElseGet(() -> createCartForCustomer(customer));
    }

    private Cart getCartModelForCurrentUser() {
        // CORRECTED: Directly get the UUID code required by the next line.
        UUID customerCode = authenticationService.getAuthenticatedCustomerCode();

        // Now the 'customerCode' variable exists and can be used here.
        Customer customer = customerService.findCustomerEntityByCode(customerCode);

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
        if (cart.getItems() == null) {
            cart.setTotalPrice(BigDecimal.ZERO);
            return;
        }
        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
    }
}
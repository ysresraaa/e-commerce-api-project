package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.dto.OrderResponseDTO;
import com.ecommerce.e_commerce_api.exception.EmptyCartException;
import com.ecommerce.e_commerce_api.exception.ResourceNotFoundException;
import com.ecommerce.e_commerce_api.mapper.OrderMapper;
import com.ecommerce.e_commerce_api.model.*;
import com.ecommerce.e_commerce_api.repository.OrderRepository;
import com.ecommerce.e_commerce_api.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final CustomerService customerService;
    private final AuthenticationService authenticationService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponseDTO placeOrderForCurrentUser() {
        UUID customerCode = authenticationService.getAuthenticatedCustomerCode();
        Customer authenticatedCustomer = customerService.findCustomerEntityByCode(customerCode);
        Cart cart = cartService.getCartModelByCustomerCode(customerCode);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cannot create order from an empty cart.");
        }

        Order order = new Order();
        order.setCustomer(authenticatedCustomer);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderCode(UUID.randomUUID());

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            productService.decreaseStock(product.getProductCode(), cartItem.getQuantity());
            return OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .order(order)
                    .build();
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        cartService.clearCartForCurrentUser();
        return orderMapper.toResponseDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getOrdersForCurrentUser(Pageable pageable) {
        Long customerId = authenticationService.getAuthenticatedCustomerId();
        Page<Order> orderPage = orderRepository.findByCustomerId(customerId, pageable);
        return orderPage.map(orderMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getOrdersByCustomerCode(UUID customerCode, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByCustomerCustomerCode(customerCode, pageable);
        return orderPage.map(orderMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderByOrderCode(UUID orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .map(orderMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with code: " + orderCode));
    }

    public boolean isOwnerOfOrder(Authentication authentication, UUID orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode).orElse(null);
        if (order == null) {
            return false;
        }
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return order.getCustomer().getId().equals(principal.getId());
    }
}
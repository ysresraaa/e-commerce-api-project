package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.dto.OrderResponseDTO;
import com.ecommerce.e_commerce_api.exception.EmptyCartException;
import com.ecommerce.e_commerce_api.exception.InsufficientStockException;
import com.ecommerce.e_commerce_api.exception.ResourceNotFoundException;
import com.ecommerce.e_commerce_api.mapper.OrderMapper;
import com.ecommerce.e_commerce_api.model.*;
import com.ecommerce.e_commerce_api.repository.IOrderRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final IOrderRepository orderRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;

    public OrderService(IOrderRepository orderRepository, CartService cartService, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.orderMapper = orderMapper;
    }


    @Transactional
    public OrderResponseDTO placeOrderForCurrentUser() {
        Customer authenticatedCustomer = getAuthenticatedCustomer();
        return placeOrder(authenticatedCustomer);
    }


    public List<OrderResponseDTO> getAllOrdersForCurrentUser() {
        Customer authenticatedCustomer = getAuthenticatedCustomer();
        List<Order> orders = orderRepository.findAllByCustomer_Id(authenticatedCustomer.getId());
        return orderMapper.toResponseDTOList(orders);
    }


    public OrderResponseDTO getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }


    public OrderResponseDTO getOrderByOrderCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .map(orderMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with code: " + orderCode));
    }


    public boolean isOwnerOfOrder(Long orderId, Long customerId) {
        return orderRepository.findById(orderId)
                .map(order -> order.getCustomer().getId().equals(customerId))
                .orElse(false);
    }

    public boolean isOwnerOfOrderCode(String orderCode, Long customerId) {
        return orderRepository.findByOrderCode(orderCode)
                .map(order -> order.getCustomer().getId().equals(customerId))
                .orElse(false);
    }

    private Customer getAuthenticatedCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof Customer)) {
            throw new org.springframework.security.access.AccessDeniedException("User not authenticated.");
        }
        return (Customer) authentication.getPrincipal();
    }

    @Transactional
    protected OrderResponseDTO placeOrder(Customer customer) {
        Cart cart = cartService.getCartModelByCustomerId(customer.getId());
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cannot create order from an empty cart.");
        }
        String uniqueOrderCode = "ECOM-" + UUID.randomUUID().toString().toUpperCase().substring(0, 11);
        Order order = Order.builder()
                .customer(customer)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalPrice(cart.getTotalPrice())
                .orderCode(uniqueOrderCode)
                .build();
        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            int requestedQuantity = cartItem.getQuantity();
            if (product.getStock() < requestedQuantity) {
                throw new InsufficientStockException("Not enough stock for product: " + product.getName());
            }
            product.setStock(product.getStock() - requestedQuantity);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(requestedQuantity);
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setOrder(order);
            return orderItem;
        }).collect(Collectors.toList());

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        cartService.clearCartForCurrentUser();

        return orderMapper.toResponseDTO(savedOrder);
    }
}
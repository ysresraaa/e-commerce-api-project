package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.dto.OrderResponseDTO;
import com.ecommerce.e_commerce_api.exception.EmptyCartException;
import com.ecommerce.e_commerce_api.exception.InsufficientStockException;
import com.ecommerce.e_commerce_api.exception.ResourceNotFoundException;
import com.ecommerce.e_commerce_api.mapper.OrderMapper;
import com.ecommerce.e_commerce_api.model.*;
import com.ecommerce.e_commerce_api.repository.IOrderRepository;
import com.ecommerce.e_commerce_api.repository.IProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final IOrderRepository orderRepository;
    private final CustomerService customerService;
    private final CartService cartService;
    private final IProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderService(IOrderRepository orderRepository, CustomerService customerService, CartService cartService, IProductRepository productRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponseDTO placeOrder(Long customerId) {
        Cart cart = cartService.getCartModelByCustomerId(customerId);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cannot create order from an empty cart.");
        }


        String uniqueOrderCode = "ECOM-" + UUID.randomUUID().toString().toUpperCase().substring(0, 11);


        Order order = Order.builder()
                .customer(cart.getCustomer())
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
        cartService.clearCart(customerId);

        return orderMapper.toResponseDTO(savedOrder);
    }

    public List<OrderResponseDTO> getAllOrdersForCustomer(Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        List<Order> orders = orderRepository.findAllByCustomer_Id(customerId);
        return orderMapper.toResponseDTOList(orders);
    }

    public OrderResponseDTO getOrderByIdAndCustomerId(Long orderId, Long customerId) {
        Order order = orderRepository.findByIdAndCustomer_Id(orderId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId + " for this customer."));
        return orderMapper.toResponseDTO(order);
    }


    public OrderResponseDTO getOrderByOrderCodeAndCustomerId(String orderCode, Long customerId) {

        Order order = orderRepository.findByOrderCodeAndCustomer_Id(orderCode, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with code: " + orderCode + " for this customer."));
        return orderMapper.toResponseDTO(order);
    }
}
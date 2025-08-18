package com.ecommerce.e_commerce_api.mapper;

import com.ecommerce.e_commerce_api.dto.OrderItemResponseDTO;
import com.ecommerce.e_commerce_api.dto.OrderResponseDTO;
import com.ecommerce.e_commerce_api.model.Order;
import com.ecommerce.e_commerce_api.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public List<OrderResponseDTO> toResponseDTOList(List<Order> orders) {
        if (orders == null) {
            return Collections.emptyList();
        }
        return orders.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO toResponseDTO(Order order) {
        if (order == null) {
            return null;
        }

        Long customerId = (order.getCustomer() != null) ? order.getCustomer().getId() : null;


        String customerName = (order.getCustomer() != null) ? order.getCustomer().getFullName() : null;

        List<OrderItemResponseDTO> itemDTOs = (order.getItems() != null)
                ? order.getItems().stream().map(this::toOrderItemResponseDTO).collect(Collectors.toList())
                : Collections.emptyList();

        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .totalPrice(order.getTotalPrice())
                .customerId(customerId)
                .customerName(customerName)
                .items(itemDTOs)
                .build();
    }

    private OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        Long productId = (orderItem.getProduct() != null) ? orderItem.getProduct().getId() : null;
        String productName = (orderItem.getProduct() != null) ? orderItem.getProduct().getName() : null;


        return OrderItemResponseDTO.builder()
                .productId(productId)
                .productName(productName)
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }
}
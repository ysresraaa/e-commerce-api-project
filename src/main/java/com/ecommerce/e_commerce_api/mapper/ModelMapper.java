package com.ecommerce.e_commerce_api.mapper;

import com.ecommerce.e_commerce_api.dto.CustomerResponseDTO;
import com.ecommerce.e_commerce_api.dto.OrderItemResponseDTO;
import com.ecommerce.e_commerce_api.dto.OrderResponseDTO;
import com.ecommerce.e_commerce_api.dto.ProductResponseDTO;
import com.ecommerce.e_commerce_api.model.Customer;
import com.ecommerce.e_commerce_api.model.Order;
import com.ecommerce.e_commerce_api.model.OrderItem;
import com.ecommerce.e_commerce_api.model.Product;

import java.util.stream.Collectors;

public class ModelMapper {

    public static ProductResponseDTO toProductResponse(Product product) {
        return ProductResponseDTO.builder()
                .productCode(product.getProductCode().toString())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

    public static CustomerResponseDTO toCustomerResponse(Customer customer) {
        String fullName = customer.getFirstName() + " " + customer.getLastName();

        return CustomerResponseDTO.builder()
                .customerCode(customer.getCustomerCode().toString())
                .fullName(fullName)
                .email(customer.getEmail())
                .build();
    }

    public static OrderItemResponseDTO toOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponseDTO.builder()
                .productCode(orderItem.getProduct().getProductCode().toString())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPriceAtPurchase())
                .build();
    }

    public static OrderResponseDTO toOrderResponse(Order order) {
        return OrderResponseDTO.builder()
                .customerCode(order.getCustomer().getCustomerCode().toString())
                .customerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName())
                .items(order.getOrderItems().stream()
                        .map(ModelMapper::toOrderItemResponse)
                        .collect(Collectors.toList()))
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .orderCode(order.getOrderCode().toString())
                .build();
    }
}
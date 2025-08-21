package com.ecommerce.e_commerce_api.mapper;

import com.ecommerce.e_commerce_api.dto.OrderItemResponseDTO;
import com.ecommerce.e_commerce_api.dto.OrderResponseDTO;
import com.ecommerce.e_commerce_api.model.Order;
import com.ecommerce.e_commerce_api.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    List<OrderResponseDTO> toResponseDTOList(List<Order> orders);

    @Mapping(target = "customerName", expression = "java(order.getCustomer().getFirstName() + \" \" + order.getCustomer().getLastName())")
    @Mapping(source = "customer.customerCode", target = "customerCode")
    @Mapping(source = "orderItems", target = "items")
    @Mapping(source = "orderCode", target = "orderCode")
    OrderResponseDTO toResponseDTO(Order order);


    @Mapping(source = "product.productCode", target = "productCode")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "priceAtPurchase", target = "price")
    OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem);
}
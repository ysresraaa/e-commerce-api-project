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

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.fullName", target = "customerName")
    OrderResponseDTO toResponseDTO(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem);
}
package com.ecommerce.e_commerce_api.dto;

import com.ecommerce.e_commerce_api.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponseDTO {

    private Long id;
    private Long customerId;
    private String customerName;
    private List<OrderItemResponseDTO> items;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private String orderCode;




}

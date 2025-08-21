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

    private String customerCode;
    private String customerName;
    private List<OrderItemResponseDTO> items;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private String orderCode;
    private BigDecimal originalTotalPrice;
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;

}
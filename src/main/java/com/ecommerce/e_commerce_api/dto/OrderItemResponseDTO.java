package com.ecommerce.e_commerce_api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponseDTO {
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
}

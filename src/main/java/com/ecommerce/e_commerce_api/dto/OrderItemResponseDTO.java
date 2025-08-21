package com.ecommerce.e_commerce_api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponseDTO {
    private String productCode;
    private String productName;
    private Integer quantity;
    private BigDecimal originalPrice;
    private BigDecimal price;
    private BigDecimal itemDiscount;
    private BigDecimal subTotal;
}

package com.ecommerce.e_commerce_api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductResponseDTO {

    private String productCode;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
}

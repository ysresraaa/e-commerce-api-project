package com.ecommerce.e_commerce_api.dto;

import lombok.Data;

@Data
public class AddToCartRequestDTO {
    private Long productId;
    private int quantity;
}
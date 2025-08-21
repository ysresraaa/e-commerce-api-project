package com.ecommerce.e_commerce_api.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AddItemRequest {

    @Min(value = 1,message="Quantity must be at least 1")
    private int quantity;
}

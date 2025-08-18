package com.ecommerce.e_commerce_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateProductRequestDTO {

    @NotBlank(message = "Product name cannot be blank.")
    private String name;

    private String description;

    @NotNull(message = "Price cannot be null.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero.")
    private BigDecimal price;

    @NotNull(message = "Stock cannot be null.")
    @PositiveOrZero(message = "Stock must be zero or greater.")
    private int stock;
}
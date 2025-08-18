package com.ecommerce.e_commerce_api.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCustomerRequestDTO {

    @Size(min = 2, message = "Full name must be at least 2 characters long")
    private String fullName;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

}
package com.ecommerce.e_commerce_api.dto;

import lombok.Data;

@Data
public class CreateCustomerRequestDTO {
    private String firstName;
    private String lastName;
    private String email;

}
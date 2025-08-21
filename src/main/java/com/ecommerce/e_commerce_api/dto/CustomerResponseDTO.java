package com.ecommerce.e_commerce_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponseDTO {

    private String customerCode;
    private String fullName;
    private String email;

}
package com.ecommerce.e_commerce_api.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String contactName;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String phone;

}

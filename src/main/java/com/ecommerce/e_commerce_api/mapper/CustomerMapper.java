package com.ecommerce.e_commerce_api.mapper;

import com.ecommerce.e_commerce_api.dto.CustomerResponseDTO;
import com.ecommerce.e_commerce_api.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {


    @Mapping(target = "fullName", expression = "java(customer.getFirstName() + \" \" + customer.getLastName())")
    CustomerResponseDTO toResponseDTO(Customer customer);


    List<CustomerResponseDTO> toResponseDTOList(List<Customer> customers);
}
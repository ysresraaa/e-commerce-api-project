package com.ecommerce.e_commerce_api.controller;

import com.ecommerce.e_commerce_api.dto.CustomerResponseDTO;
import com.ecommerce.e_commerce_api.dto.UpdateCustomerRequestDTO;
import com.ecommerce.e_commerce_api.model.Customer;
import com.ecommerce.e_commerce_api.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping("/me")
    public ResponseEntity<CustomerResponseDTO> getMyProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Customer authenticatedCustomer = (Customer) authentication.getPrincipal();
        Long customerId = authenticatedCustomer.getId();


        CustomerResponseDTO customerDTO = customerService.getCustomerProfileById(customerId);
        return ResponseEntity.ok(customerDTO);
    }


    @PutMapping("/me")
    public ResponseEntity<CustomerResponseDTO> updateMyProfile(@Valid @RequestBody UpdateCustomerRequestDTO requestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Customer authenticatedCustomer = (Customer) authentication.getPrincipal();
        Long customerId = authenticatedCustomer.getId();

        CustomerResponseDTO updatedCustomer = customerService.updateCustomerProfile(customerId, requestDTO);
        return ResponseEntity.ok(updatedCustomer);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> getCustomerByIdForAdmin(@PathVariable Long id) {
        CustomerResponseDTO customerDTO = customerService.getCustomerProfileById(id);
        return ResponseEntity.ok(customerDTO);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
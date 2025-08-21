package com.ecommerce.e_commerce_api.controller;

import com.ecommerce.e_commerce_api.dto.CustomerResponseDTO;
import com.ecommerce.e_commerce_api.dto.UpdateCustomerRequest;
import com.ecommerce.e_commerce_api.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }


    @PutMapping("/{customerCode}")

    @PreAuthorize("hasRole('ADMIN') or #customerCode == authentication.principal.customerCode")
    public ResponseEntity<CustomerResponseDTO> updateCustomerProfile(
            @PathVariable UUID customerCode,
            @Valid @RequestBody UpdateCustomerRequest requestDTO) {

        CustomerResponseDTO updatedCustomer = customerService.updateCustomerProfile(customerCode, requestDTO);
        return ResponseEntity.ok(updatedCustomer);
    }


    @GetMapping("/{customerCode}")

    @PreAuthorize("hasRole('ADMIN') or #customerCode == authentication.principal.customerCode")
    public ResponseEntity<CustomerResponseDTO> getCustomerProfile(@PathVariable UUID customerCode) {

        CustomerResponseDTO customerDTO = customerService.getCustomerProfileByCode(customerCode);
        return ResponseEntity.ok(customerDTO);
    }

    @DeleteMapping("/{customerCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID customerCode) {

        customerService.deleteCustomer(customerCode);
        return ResponseEntity.noContent().build();
    }
}
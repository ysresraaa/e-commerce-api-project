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

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerResponseDTO>>getAllCustomers() {

        return ResponseEntity.ok(customerService.getAllCustomers());
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id==authentication.principal.id")
    public ResponseEntity<CustomerResponseDTO> updateCustomerProfile( @PathVariable Long id,@Valid @RequestBody UpdateCustomerRequestDTO requestDTO) {
        CustomerResponseDTO updatedCustomer=customerService.updateCustomerProfile(id,requestDTO);
        return ResponseEntity.ok(updatedCustomer);


    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id==authentication.principal.id ")
    public ResponseEntity<CustomerResponseDTO> getCustomeProfile(@PathVariable Long id) {
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
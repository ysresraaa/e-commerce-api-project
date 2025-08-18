// CustomerController.java
package com.ecommerce.e_commerce_api.controller;

import com.ecommerce.e_commerce_api.dto.CreateCustomerRequestDTO;
import com.ecommerce.e_commerce_api.model.Customer;
import com.ecommerce.e_commerce_api.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    private Long getAuthenticatedCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new AccessDeniedException("User not authenticated.");
        }
        Customer authenticatedCustomer = (Customer) authentication.getPrincipal();
        return authenticatedCustomer.getId();
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Customer> createCustomer(@RequestBody CreateCustomerRequestDTO request) {
        Customer newCustomer = customerService.createCustomer(request);
        return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);
    }


    @GetMapping("/me")
    public ResponseEntity<Customer> getMyCustomerProfile() {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        Customer customer = customerService.getCustomerById(authenticatedCustomerId);
        return ResponseEntity.ok(customer);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(customer);
    }


    @PutMapping("/me")
    public ResponseEntity<Customer> updateMyCustomerProfile(@RequestBody Customer customerDetails) {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();

        customerDetails.setId(authenticatedCustomerId);
        Customer updatedCustomer = customerService.updateCustomer(authenticatedCustomerId, customerDetails);
        return ResponseEntity.ok(updatedCustomer);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long customerId, @RequestBody Customer customerDetails) {
        Customer updatedCustomer = customerService.updateCustomer(customerId, customerDetails);
        return ResponseEntity.ok(updatedCustomer);
    }


    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyCustomerAccount() {
        Long authenticatedCustomerId = getAuthenticatedCustomerId();
        customerService.deleteCustomer(authenticatedCustomerId);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}
package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.dto.CreateCustomerRequestDTO;
import com.ecommerce.e_commerce_api.dto.CustomerResponseDTO;
import com.ecommerce.e_commerce_api.dto.UpdateCustomerRequest;
import com.ecommerce.e_commerce_api.exception.DuplicateResourceException;
import com.ecommerce.e_commerce_api.exception.ResourceNotFoundException;
import com.ecommerce.e_commerce_api.mapper.CustomerMapper;
import com.ecommerce.e_commerce_api.model.Customer;
import com.ecommerce.e_commerce_api.model.Role;
import com.ecommerce.e_commerce_api.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customerMapper.toResponseDTOList(customers);
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerProfileByCode(UUID customerCode) {
        Customer customer = findCustomerEntityByCode(customerCode);
        return customerMapper.toResponseDTO(customer);
    }

    @Transactional
    public CustomerResponseDTO updateCustomerProfile(UUID customerCode, UpdateCustomerRequest requestDTO) {
        Customer existingCustomer = findCustomerEntityByCode(customerCode);

        if (requestDTO.getFullName() != null && !requestDTO.getFullName().isBlank()) {
            String[] nameParts = requestDTO.getFullName().trim().split("\\s+", 2);
            existingCustomer.setFirstName(nameParts[0]);
            existingCustomer.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        }

        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isBlank()) {
            existingCustomer.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return customerMapper.toResponseDTO(updatedCustomer);
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CreateCustomerRequestDTO requestDTO) {
        if (customerRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already in use: " + requestDTO.getEmail());
        }

        Customer newCustomer = new Customer();
        newCustomer.setCustomerCode(UUID.randomUUID());
        newCustomer.setFirstName(requestDTO.getFirstName());
        newCustomer.setLastName(requestDTO.getLastName());
        newCustomer.setEmail(requestDTO.getEmail());
        newCustomer.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        newCustomer.setRole(Role.USER);

        Customer savedCustomer = customerRepository.save(newCustomer);
        return customerMapper.toResponseDTO(savedCustomer);
    }

    @Transactional
    public void deleteCustomer(UUID customerCode) {
        Customer customerToDelete = findCustomerEntityByCode(customerCode);
        customerRepository.delete(customerToDelete);
    }

    public Customer findCustomerEntityByCode(UUID customerCode) {
        return customerRepository.findByCustomerCode(customerCode)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with code: " + customerCode));
    }
}
package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.dto.CreateCustomerRequestDTO;
import com.ecommerce.e_commerce_api.dto.CustomerResponseDTO;
import com.ecommerce.e_commerce_api.dto.UpdateCustomerRequestDTO;
import com.ecommerce.e_commerce_api.exception.DuplicateResourceException;
import com.ecommerce.e_commerce_api.exception.ResourceNotFoundException;
import com.ecommerce.e_commerce_api.mapper.CustomerMapper;
import com.ecommerce.e_commerce_api.model.Customer;
import com.ecommerce.e_commerce_api.model.Role;
import com.ecommerce.e_commerce_api.repository.ICustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class CustomerService {

    private final ICustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(ICustomerRepository customerRepository, CustomerMapper customerMapper, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.passwordEncoder = passwordEncoder;
    }



    @Transactional
    public CustomerResponseDTO getCustomerProfileById(Long customerId) {
        Customer customer = getCustomerById(customerId);
        return customerMapper.toResponseDTO(customer);
    }

    @Transactional
    public CustomerResponseDTO updateCustomerProfile(Long customerId, UpdateCustomerRequestDTO requestDTO) {
        Customer existingCustomer = getCustomerById(customerId);


        if (requestDTO.getFullName() != null && !requestDTO.getFullName().isBlank()) {

            String[] nameParts = requestDTO.getFullName().trim().split("\\s+", 2);
            existingCustomer.setFirstName(nameParts[0]);
            if (nameParts.length > 1) {
                existingCustomer.setLastName(nameParts[1]);
            } else {
                existingCustomer.setLastName("");
            }
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
        newCustomer.setFirstName(requestDTO.getFirstName());
        newCustomer.setLastName(requestDTO.getLastName());
        newCustomer.setEmail(requestDTO.getEmail());

        newCustomer.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        newCustomer.setRole(Role.USER);

        Customer savedCustomer = customerRepository.save(newCustomer);
        return customerMapper.toResponseDTO(savedCustomer);
    }

    public void deleteCustomer(Long id) {

        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id:" + id);
        }
        customerRepository.deleteById(id);
    }


    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id:" + id));
    }
}
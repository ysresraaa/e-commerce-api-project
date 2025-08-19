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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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


    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customerMapper.toResponseDTOList(customers);
    }


    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerProfileById(Long customerId) {
        Customer customer = findCustomerEntityById(customerId);
        return customerMapper.toResponseDTO(customer);
    }


    @Transactional
    public CustomerResponseDTO updateCustomerProfile(Long customerId, UpdateCustomerRequestDTO requestDTO) {
        Customer existingCustomer = findCustomerEntityById(customerId);


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
        newCustomer.setFirstName(requestDTO.getFirstName());
        newCustomer.setLastName(requestDTO.getLastName());
        newCustomer.setEmail(requestDTO.getEmail());
        newCustomer.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        newCustomer.setRole(Role.USER);

        Customer savedCustomer = customerRepository.save(newCustomer);
        return customerMapper.toResponseDTO(savedCustomer);
    }


    @Transactional
    public void deleteCustomer(Long id) {
        Customer customerToDelete = findCustomerEntityById(id);
        customerRepository.delete(customerToDelete);
    }

    public Customer findCustomerEntityById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }
}
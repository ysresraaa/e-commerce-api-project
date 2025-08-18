package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.dto.CreateCustomerRequestDTO;
import com.ecommerce.e_commerce_api.exception.DuplicateResourceException;
import com.ecommerce.e_commerce_api.exception.ResourceNotFoundException;
import com.ecommerce.e_commerce_api.model.Customer;
import com.ecommerce.e_commerce_api.repository.ICustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final ICustomerRepository customerRepository;

    public CustomerService(ICustomerRepository customerRepository ){
        this.customerRepository=customerRepository;
    }

    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }
    public Customer getCustomerById(Long id){
        return customerRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Customer not found with id:"+id));
    }

    public Customer getCustomerByEmail(String email){
        return customerRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("Customer not found with email:"+email));

    }


    @Transactional
    public Customer createCustomer(CreateCustomerRequestDTO requestDTO) {
        Optional<Customer> existingCustomer = customerRepository.findByEmail(requestDTO.getEmail());
        if (existingCustomer.isPresent()) {
            throw new DuplicateResourceException("Email already in use: " + requestDTO.getEmail());
        }

        Customer newCustomer = new Customer();
        newCustomer.setFirstName(requestDTO.getFirstName());
        newCustomer.setLastName(requestDTO.getLastName());
        newCustomer.setEmail(requestDTO.getEmail());


        return customerRepository.save(newCustomer);
    }

    @Transactional
    public Customer updateCustomer(Long id,Customer customerDetails){
        Customer existingCustomer=getCustomerById(id);

        if(customerDetails.getEmail()!=null && !customerDetails.getEmail().equals(existingCustomer.getEmail())){
            if(customerRepository.findByEmail(customerDetails.getEmail()).isPresent()){
                throw new DuplicateResourceException("Email already in use :"+customerDetails.getEmail());

            }
            existingCustomer.setEmail(customerDetails.getEmail());
        }
        existingCustomer.setFirstName(customerDetails.getFirstName());
        existingCustomer.setLastName(customerDetails.getLastName());

        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(Long id){
        Customer existingCustomer=getCustomerById(id);
        customerRepository.delete(existingCustomer);
    }










}

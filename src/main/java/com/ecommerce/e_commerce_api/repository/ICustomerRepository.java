package com.ecommerce.e_commerce_api.repository;

import com.ecommerce.e_commerce_api.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer,Long> {

    Optional<Customer>findByEmail(String email);


}

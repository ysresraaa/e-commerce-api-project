package com.ecommerce.e_commerce_api.repository;

import com.ecommerce.e_commerce_api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {


    Optional<Order> findByIdAndCustomer_Id(Long id, Long customerId);

    List<Order> findAllByCustomer_Id(Long customerId);

    Optional<Order> findByOrderCodeAndCustomer_Id(String orderCode, Long customerId);

}
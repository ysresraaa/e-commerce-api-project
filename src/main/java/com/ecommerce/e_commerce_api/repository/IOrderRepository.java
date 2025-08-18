
package com.ecommerce.e_commerce_api.repository;

import com.ecommerce.e_commerce_api.model.Customer;
import com.ecommerce.e_commerce_api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IOrderRepository  extends JpaRepository<Order,Long> {

    List<Order> findByCustomer(Customer customer);
    Optional<Order> findByOrderCode(String orderCode);
}
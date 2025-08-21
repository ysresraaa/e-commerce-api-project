package com.ecommerce.e_commerce_api.repository;

import com.ecommerce.e_commerce_api.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepositroy extends JpaRepository<OrderItem,Long> {
}

package com.ecommerce.e_commerce_api.repository;

import com.ecommerce.e_commerce_api.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

}

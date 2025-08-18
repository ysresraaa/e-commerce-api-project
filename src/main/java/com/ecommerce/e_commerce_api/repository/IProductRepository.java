package com.ecommerce.e_commerce_api.repository;

import com.ecommerce.e_commerce_api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductRepository  extends JpaRepository<Product,Long> {
}

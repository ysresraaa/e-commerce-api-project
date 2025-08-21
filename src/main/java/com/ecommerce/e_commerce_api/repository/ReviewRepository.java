package com.ecommerce.e_commerce_api.repository;

import com.ecommerce.e_commerce_api.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

    List<Review> findByProduct_IdOrderByCreatedAtDesc(Long productId);

    List<Review>findByProduct_Id(Long productId);

    List<Review>findByCustomer_Id(Long customerId);

    Optional<Review>findByProductIdAndCustomerId(Long productId,Long customerId);


}

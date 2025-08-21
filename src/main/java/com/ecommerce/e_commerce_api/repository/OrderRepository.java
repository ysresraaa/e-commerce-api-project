package com.ecommerce.e_commerce_api.repository;

import com.ecommerce.e_commerce_api.model.Order;
import com.ecommerce.e_commerce_api.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(UUID orderCode);

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    Page<Order> findByCustomerCustomerCode(UUID customerCode, Pageable pageable);

    List<Order> findAllByCustomer_Id(Long customerId);

    long countByOrderDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.orderItems oi " +
            "WHERE o.customer.id = :customerId " +
            "AND oi.product.productCode = :productCode " +
            "AND o.status IN :statuses")
    boolean hasCustomerPurchasedProduct(
            @Param("customerId") Long customerId,
            @Param("productCode") UUID productCode,
            @Param("statuses") Set<OrderStatus> statuses
    );
}
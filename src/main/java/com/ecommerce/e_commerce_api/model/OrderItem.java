package com.ecommerce.e_commerce_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name="order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem  extends BaseEntity{

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="order_id",nullable=false)
    private Order order;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="product_id",nullable=false)
    private Product product;

    @Column(nullable=false)
    private Integer quantity;

    @Column(nullable=false,precision=10,scale=2)
    private BigDecimal price;



}

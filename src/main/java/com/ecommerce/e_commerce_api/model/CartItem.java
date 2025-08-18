package com.ecommerce.e_commerce_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name="cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem extends BaseEntity{

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="cart_id",nullable=false)
    private Cart cart;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="product_id",nullable=false)
    private Product product;

    @Column(nullable=false)
    private int quantity;

    @Column(nullable=false,precision=10,scale=2)
    private BigDecimal price;

}

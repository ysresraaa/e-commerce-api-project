package com.ecommerce.e_commerce_api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends BaseEntity {

    @OneToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="customer_id",nullable=false)
    private Customer customer;

    @OneToMany(mappedBy="cart",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<CartItem> items=new HashSet<>();

    @Column(nullable = false)
    private BigDecimal totalPrice=BigDecimal.ZERO;


}

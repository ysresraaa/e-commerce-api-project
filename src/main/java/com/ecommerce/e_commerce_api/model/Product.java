package com.ecommerce.e_commerce_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name="products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends BaseEntity{

    @Column(nullable=false,length=200)
    private String name;

    @Lob
    private String description;

    @Column(nullable=false,precision=10,scale=2)
    private BigDecimal price;

    @Column(nullable=false)
    private Integer stock;
}

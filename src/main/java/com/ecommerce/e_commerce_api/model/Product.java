package com.ecommerce.e_commerce_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends BaseEntity{

    @Column(name = "product_code",unique = true,nullable = false,updatable = false)
    private UUID productCode;

    @Column(name="sku",nullable = false,unique = true,length = 50)
    private String sku;

    @Column(nullable=false,length=200)
    private String name;

    @Lob
    private String description;

    @Column(nullable=false,precision=10,scale=2)
    private BigDecimal price;

    @Column(nullable=false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @ElementCollection
    @CollectionTable(name = "product_images",joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String>imageUrls=new ArrayList<>();

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Review>reviews=new ArrayList<>();

    @PrePersist
    public void generateProductCode()
    {
        if(this.productCode==null){
            this.productCode=UUID.randomUUID();
        }
    }









}

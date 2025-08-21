package com.ecommerce.e_commerce_api.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false,exclude = "order")
public class OrderItem  extends BaseEntity{

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="order_id",nullable=false)
    private Order order;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="product_id",nullable=false)
    private Product product;

    @OneToMany(mappedBy = "orderItem",cascade =CascadeType.ALL)
    private List<ShipmentItem>shipmentItems=new ArrayList<>();

    @Column(nullable=false)
    private Integer quantity;

    @Column(name = "original_price",nullable = false,precision = 10,scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "price_at_purchase", nullable=false,precision=10,scale=2)
    private BigDecimal priceAtPurchase;

    @Transient
    public BigDecimal getSubTotal(){
        if (priceAtPurchase==null || quantity==null){
            return BigDecimal.ZERO;
        }

        return priceAtPurchase.multiply(new BigDecimal(quantity));
    }
    @Transient
    public BigDecimal getItemDiscount() {
        if (originalPrice == null || priceAtPurchase == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal singleItemDiscount = originalPrice.subtract(priceAtPurchase);
        return singleItemDiscount.multiply(new BigDecimal(quantity));
    }



}

package com.ecommerce.e_commerce_api.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {


    @Column(name = "order_code", unique = true, nullable = false, updatable = false)
    private UUID orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Shipment> shipments = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "contactName", column = @Column(name = "shipping_contact_name")),
            @AttributeOverride(name = "street", column = @Column(name = "shipping_street")),
            @AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
            @AttributeOverride(name = "state", column = @Column(name = "shipping_state")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "shipping_zip_code")),
            @AttributeOverride(name = "country", column = @Column(name = "shipping_country")),
            @AttributeOverride(name = "phone", column = @Column(name = "shipping_phone"))
    })
    private Address shippingAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "contactName", column = @Column(name = "billing_contact_name")),
            @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
            @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
            @AttributeOverride(name = "state", column = @Column(name = "billing_state")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "billing_zip_code")),
            @AttributeOverride(name = "country", column = @Column(name = "billing_country")),
            @AttributeOverride(name = "phone", column = @Column(name = "billing_phone"))
    })
    private Address billingAddress;

    @Column(name = "original_total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalTotalPrice;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "promotion_code")
    private String promotionCode;

    @Column(name = "promotion_discount", precision = 10, scale = 2)
    private BigDecimal promotionDiscount;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_transaction_id", unique = true)
    private String paymentTransactionId;

    @Column(name = "card_last_four_digits")
    private String cardLastFourDigits;

    @Column(name = "card_brand")
    private String cardBrand;

    @Column(name = "customer_ip_address")
    private String customerIpAddress;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    public void calculateTotals() {
        if (this.orderItems == null) {
            this.originalTotalPrice = BigDecimal.ZERO;
            this.totalPrice = BigDecimal.ZERO;
            return;
        }

        this.originalTotalPrice = this.orderItems.stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal finalPrice = this.originalTotalPrice;

        if (this.promotionDiscount != null && this.promotionDiscount.compareTo(BigDecimal.ZERO) > 0) {
            finalPrice = finalPrice.subtract(this.promotionDiscount);
        }

        this.totalPrice = finalPrice.max(BigDecimal.ZERO);
    }

    @Transient
    public BigDecimal getTotalDiscount() {
        if (originalTotalPrice == null || totalPrice == null) {
            return BigDecimal.ZERO;
        }
        return originalTotalPrice.subtract(totalPrice);
    }

    @PrePersist
    protected void onPrePersist() {

        if (this.orderDate == null) {
            this.orderDate = LocalDateTime.now();
        }
        calculateTotals();
    }

    @PreUpdate
    protected void onPreUpdate() {
        calculateTotals();
    }
}
package com.ecommerce.e_commerce_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipment extends BaseEntity {

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShipmentItem>shipmentItems=new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private  ShipmentStatus  status;

    @Column(name ="tracking_number")
    private String trackingNumber;

    @Column(name = "carrier")
    private String caririer;

    @Column(name = "shipped_date")
    private String shippedDate;





}

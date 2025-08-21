package com.ecommerce.e_commerce_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipment_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentItem {

    @EmbeddedId
    private ShipmentItemKey id;

    @ManyToOne(fetch= FetchType.LAZY)
    @MapsId("shipmentId")
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderItemId")
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @Column(nullable = false)
    private Integer quantity;


}

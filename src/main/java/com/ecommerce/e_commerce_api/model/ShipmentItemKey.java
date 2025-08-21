package com.ecommerce.e_commerce_api.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ShipmentItemKey implements Serializable {

    private Long shipmentId;
    private Long orderItemId;
}


package com.ecommerce.e_commerce_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDTO {
    private Long id;
    private Long customerId;
    private BigDecimal totalPrice;
    private List<CartItemResponseDTO> items;
}
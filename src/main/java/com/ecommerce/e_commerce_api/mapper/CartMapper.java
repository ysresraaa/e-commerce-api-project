package com.ecommerce.e_commerce_api.mapper;

import com.ecommerce.e_commerce_api.dto.CartItemResponseDTO;
import com.ecommerce.e_commerce_api.dto.CartResponseDTO;
import com.ecommerce.e_commerce_api.model.Cart;
import com.ecommerce.e_commerce_api.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "productPrice")
    @Mapping(target = "subtotal", expression = "java(cartItem.getProduct().getPrice().multiply(java.math.BigDecimal.valueOf(cartItem.getQuantity())))")
    CartItemResponseDTO toCartItemDto(CartItem cartItem);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "items", target = "items", qualifiedByName = "mapCartItems")
    CartResponseDTO toDto(Cart cart);


    @Named("mapCartItems")
    default java.util.List<CartItemResponseDTO> mapCartItems(java.util.Set<CartItem> cartItems) {
        if (cartItems == null) {
            return null;
        }
        return cartItems.stream()
                .map(this::toCartItemDto)
                .collect(java.util.stream.Collectors.toList());
    }
}
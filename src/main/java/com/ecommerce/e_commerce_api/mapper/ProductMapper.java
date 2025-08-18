package com.ecommerce.e_commerce_api.mapper;


import com.ecommerce.e_commerce_api.dto.CreateProductRequestDTO;
import com.ecommerce.e_commerce_api.dto.ProductResponseDTO;
import com.ecommerce.e_commerce_api.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        return product;
    }


    public ProductResponseDTO toResponseDTO(Product entity) {
        return ProductResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .build();
    }


    public List<ProductResponseDTO> toResponseDTOList(List<Product> entities) {
        return entities.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
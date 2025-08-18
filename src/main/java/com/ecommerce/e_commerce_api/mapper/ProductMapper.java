package com.ecommerce.e_commerce_api.mapper;

import com.ecommerce.e_commerce_api.dto.CreateProductRequestDTO;
import com.ecommerce.e_commerce_api.dto.ProductResponseDTO;
import com.ecommerce.e_commerce_api.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    Product toEntity(CreateProductRequestDTO dto);

    ProductResponseDTO toResponseDTO(Product entity);

    List<ProductResponseDTO> toResponseDTOList(List<Product> entities);
}
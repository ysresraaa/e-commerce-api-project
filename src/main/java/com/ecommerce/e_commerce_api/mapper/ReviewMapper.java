package com.ecommerce.e_commerce_api.mapper;

import com.ecommerce.e_commerce_api.dto.review.ReviewResponse;
import com.ecommerce.e_commerce_api.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "customer.firstName", target = "customerFirstName")
    ReviewResponse toResponseDTO(Review review);

    List<ReviewResponse> toResponseDTOList(List<Review> reviews);
}
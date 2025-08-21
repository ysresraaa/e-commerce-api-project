package com.ecommerce.e_commerce_api.dto.review;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewRequest {

    @NotNull(message = "Product ID cannot be null.")
    private Long productId;

    @NotNull(message = "Rating cannot be null")
    @Min(value=1,message = "Rating must be at least 1.")
    @Max(value=5,message = "Rating must be at most 5.")
    private Integer rating;

    @NotBlank(message = "Comment cannot be empty.")
    @Size(max = 1000,message = "Comment can be at most 1000 characters long.")
    private String comment;
}


package com.ecommerce.e_commerce_api.service;

import com.ecommerce.e_commerce_api.dto.review.CreateReviewRequest;
import com.ecommerce.e_commerce_api.dto.review.ReviewResponse;
import com.ecommerce.e_commerce_api.exception.ResourceNotFoundException;
import com.ecommerce.e_commerce_api.mapper.ReviewMapper;
import com.ecommerce.e_commerce_api.model.*;
import com.ecommerce.e_commerce_api.repository.OrderRepository;
import com.ecommerce.e_commerce_api.repository.ProductRepository;
import com.ecommerce.e_commerce_api.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ReviewMapper reviewMapper;

    public List<ReviewResponse> getReviewsForProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        List<Review> reviews = reviewRepository.findByProduct_IdOrderByCreatedAtDesc(productId);
        return reviewMapper.toResponseDTOList(reviews);
    }

    @Transactional
    public ReviewResponse addReview(CreateReviewRequest request) {
        Customer authenticatedCustomer = getAuthenticatedCustomer();
        Long customerId = authenticatedCustomer.getId();
        Long productId = request.getProductId();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));


        Set<OrderStatus> allowedStatuses = Set.of(OrderStatus.COMPLETED);

        // HATA DÜZELTİLDİ: Metoda 'productId' (Long) yerine 'product.getProductCode()' (UUID) gönderildi.
        boolean hasPurchased = orderRepository.hasCustomerPurchasedProduct(customerId, product.getProductCode(), allowedStatuses);

        if (!hasPurchased) {
            throw new AccessDeniedException("You can only review products that you have purchased and received.");
        }

        reviewRepository.findByProductIdAndCustomerId(productId, customerId)
                .ifPresent(r -> {
                    throw new IllegalStateException("You have already reviewed this product.");
                });

        Review newReview = Review.builder()
                .product(product)
                .customer(authenticatedCustomer)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(newReview);
        return reviewMapper.toResponseDTO(savedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Customer authenticatedCustomer = getAuthenticatedCustomer();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (!review.getCustomer().getId().equals(authenticatedCustomer.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this review.");
        }

        reviewRepository.delete(review);
    }

    private Customer getAuthenticatedCustomer() {
        return (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
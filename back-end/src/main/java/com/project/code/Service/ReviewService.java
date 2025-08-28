package com.project.code.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.code.Model.Review;
import com.project.code.Repo.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Review createReview(Review review) {
        log.info("Creating new review for product {} by customer {}", review.getProductId(), review.getCustomerId());
        return reviewRepository.save(review);
    }

    public List<Review> getAllReviews() {
        log.info("Getting all reviews");
        return reviewRepository.findAll();
    }

    public Optional<Review> getReviewById(String id) {
        log.info("Getting review by ID: {}", id);
        return reviewRepository.findById(id);
    }

    public List<Review> getReviewsByStoreAndProduct(Long storeId, Long productId) {
        log.info("Getting reviews for store {} and product {}", storeId, productId);
        return reviewRepository.findByStoreIdAndProductId(storeId, productId);
    }

    public Review updateReview(String id, Review review) {
        log.info("Updating review with ID: {}", id);
        if (reviewRepository.existsById(id)) {
            review.setId(id);
            return reviewRepository.save(review);
        }
        throw new RuntimeException("Review not found with ID: " + id);
    }

    public void deleteReview(String id) {
        log.info("Deleting review with ID: {}", id);
        reviewRepository.deleteById(id);
    }

    public List<Review> getReviewsByCustomer(Long customerId) {
        log.info("Getting reviews by customer ID: {}", customerId);
        return reviewRepository.findAll().stream()
                .filter(review -> review.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public List<Review> getReviewsByProduct(Long productId) {
        log.info("Getting reviews by product ID: {}", productId);
        return reviewRepository.findAll().stream()
                .filter(review -> review.getProductId().equals(productId))
                .collect(Collectors.toList());
    }

    public double getAverageRating(Long productId) {
        log.info("Getting average rating for product ID: {}", productId);
        List<Review> reviews = getReviewsByProduct(productId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
}

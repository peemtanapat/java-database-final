package com.project.code.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.Customer;
import com.project.code.Model.Review;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Service.ReviewService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final CustomerRepository customerRepository;

    @GetMapping("/{storeId}/{productId}")
    public ResponseEntity<Map<String, Object>> getReviews(
            @PathVariable Long storeId,
            @PathVariable Long productId) {

        try {
            List<Review> reviews = reviewService.getReviewsByStoreAndProduct(storeId, productId);

            List<Map<String, Object>> filteredReviews = reviews.stream()
                    .map(review -> {
                        Map<String, Object> reviewMap = new HashMap<>();
                        reviewMap.put("comment", review.getComment());
                        reviewMap.put("rating", review.getRating());

                        // Get customer name
                        try {
                            Customer customer = customerRepository.findById(review.getCustomerId())
                                    .orElseThrow(() -> new RuntimeException("Customer not found"));
                            reviewMap.put("customerName", customer.getName());
                        } catch (Exception e) {
                            reviewMap.put("customerName", "Unknown Customer");
                        }

                        return reviewMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("reviews", filteredReviews);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error retrieving reviews: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createReview(@RequestBody Review review) {
        try {
            Review createdReview = reviewService.createReview(review);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Review created successfully");
            response.put("review", createdReview);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error creating review: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateReview(
            @PathVariable String id,
            @RequestBody Review review) {

        try {
            Review updatedReview = reviewService.updateReview(id, review);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Review updated successfully");
            response.put("review", updatedReview);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error updating review: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable String id) {
        try {
            reviewService.deleteReview(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Review deleted successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error deleting review: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getReviewsByCustomer(@PathVariable Long customerId) {
        try {
            List<Review> reviews = reviewService.getReviewsByCustomer(customerId);

            Map<String, Object> response = new HashMap<>();
            response.put("reviews", reviews);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error retrieving customer reviews: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/api/v1/products/{productId}")
    public ResponseEntity<Map<String, Object>> getReviewsByProduct(@PathVariable Long productId) {
        try {
            List<Review> reviews = reviewService.getReviewsByProduct(productId);
            double averageRating = reviewService.getAverageRating(productId);

            Map<String, Object> response = new HashMap<>();
            response.put("reviews", reviews);
            response.put("averageRating", averageRating);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error retrieving product reviews: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

package com.project.code.Controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.project.code.Model.Customer;
import com.project.code.Model.Review;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Service.ReviewService;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private ReviewController reviewController;

    private Review review1;
    private Review review2;
    private Review review3;
    private Customer customer;

    @BeforeEach
    void setUp() {
        review1 = new Review(1L, 1L, 1L, 5, "Great product!");
        review1.setId("review1");

        review2 = new Review(2L, 1L, 1L, 4, "Good product");
        review2.setId("review2");

        review3 = new Review(1L, 2L, 1L, 3, "Average product");
        review3.setId("review3");

        customer = new Customer("John Doe", "john@example.com", "1234567890");
        customer.setId(1L);
    }

    @Test
    void getReviews_ShouldReturnFilteredReviewsWithCustomerNames() {
        // Arrange
        List<Review> reviews = Arrays.asList(review1, review2);
        when(reviewService.getReviewsByStoreAndProduct(1L, 1L)).thenReturn(reviews);
        when(customerRepository.findById(1L)).thenReturn(java.util.Optional.of(customer));
        when(customerRepository.findById(2L))
                .thenReturn(java.util.Optional.of(new Customer("Jane Doe", "jane@example.com", "0987654321")));

        // Act
        ResponseEntity<Map<String, Object>> response = reviewController.getReviews(1L, 1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("reviews");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> returnedReviews = (List<Map<String, Object>>) response.getBody().get("reviews");
        assertThat(returnedReviews).hasSize(2);

        Map<String, Object> firstReview = returnedReviews.get(0);
        assertThat(firstReview).containsKey("comment");
        assertThat(firstReview).containsKey("rating");
        assertThat(firstReview).containsKey("customerName");

        verify(reviewService).getReviewsByStoreAndProduct(1L, 1L);
        verify(customerRepository).findById(1L);
        verify(customerRepository).findById(2L);
    }

    @Test
    void getReviews_ShouldHandleCustomerNotFound() {
        // Arrange
        List<Review> reviews = Arrays.asList(review1);
        when(reviewService.getReviewsByStoreAndProduct(1L, 1L)).thenReturn(reviews);
        when(customerRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // Act
        ResponseEntity<Map<String, Object>> response = reviewController.getReviews(1L, 1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> returnedReviews = (List<Map<String, Object>>) response.getBody().get("reviews");
        assertThat(returnedReviews).hasSize(1);

        Map<String, Object> review = returnedReviews.get(0);
        assertThat(review.get("customerName")).isEqualTo("Unknown Customer");

        verify(reviewService).getReviewsByStoreAndProduct(1L, 1L);
        verify(customerRepository).findById(1L);
    }

    @Test
    void getReviews_ShouldHandleServiceException() {
        // Arrange
        when(reviewService.getReviewsByStoreAndProduct(1L, 1L))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Map<String, Object>> response = reviewController.getReviews(1L, 1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message")).asString().contains("Database error");

        verify(reviewService).getReviewsByStoreAndProduct(1L, 1L);
    }

    @Test
    void createReview_ShouldReturnCreatedReview() {
        // Arrange
        Review createdReview = new Review(1L, 1L, 1L, 5, "Great product!");
        createdReview.setId("newReviewId");
        when(reviewService.createReview(review1)).thenReturn(createdReview);

        // Act
        ResponseEntity<Map<String, Object>> response = reviewController.createReview(review1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody()).containsKey("review");
        assertThat(response.getBody().get("message")).isEqualTo("Review created successfully");

        verify(reviewService).createReview(review1);
    }

    @Test
    void createReview_ShouldHandleServiceException() {
        // Arrange
        when(reviewService.createReview(review1))
                .thenThrow(new RuntimeException("Validation error"));

        // Act
        ResponseEntity<Map<String, Object>> response = reviewController.createReview(review1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message")).asString().contains("Validation error");

        verify(reviewService).createReview(review1);
    }

    @Test
    void updateReview_ShouldReturnUpdatedReview() {
        // Arrange
        Review updatedReview = new Review(1L, 1L, 1L, 4, "Updated comment");
        updatedReview.setId("review1");
        when(reviewService.updateReview("review1", review1)).thenReturn(updatedReview);

        // Act
        ResponseEntity<Map<String, Object>> response = reviewController.updateReview("review1", review1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody()).containsKey("review");
        assertThat(response.getBody().get("message")).isEqualTo("Review updated successfully");

        verify(reviewService).updateReview("review1", review1);
    }

    @Test
    void updateReview_ShouldHandleNotFoundException() {
        // Arrange
        when(reviewService.updateReview("nonexistent", review1))
                .thenThrow(new RuntimeException("Review not found with ID: nonexistent"));

        // Act
        ResponseEntity<Map<String, Object>> response = reviewController.updateReview("nonexistent", review1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message")).isEqualTo("Review not found with ID: nonexistent");

        verify(reviewService).updateReview("nonexistent", review1);
    }

    @Test
    void deleteReview_ShouldReturnSuccessMessage() {
        // Act
        ResponseEntity<Map<String, Object>> response = reviewController.deleteReview("review1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message")).isEqualTo("Review deleted successfully");

        verify(reviewService).deleteReview("review1");
    }

    @Test
    void deleteReview_ShouldHandleServiceException() {
        // Arrange
        org.mockito.Mockito.doThrow(new RuntimeException("Delete error"))
                .when(reviewService).deleteReview("review1");

        // Act
        ResponseEntity<Map<String, Object>> response = reviewController.deleteReview("review1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message")).asString().contains("Delete error");

        verify(reviewService).deleteReview("review1");
    }

    @Test
    void getReviewsByCustomer_ShouldReturnCustomerReviews() {
        // Arrange
        List<Review> customerReviews = Arrays.asList(review1, review3);
        when(reviewService.getReviewsByCustomer(1L)).thenReturn(customerReviews);

        // Act
        ResponseEntity<Map<String, Object>> response = reviewController.getReviewsByCustomer(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("reviews");

        @SuppressWarnings("unchecked")
        List<Review> returnedReviews = (List<Review>) response.getBody().get("reviews");
        assertThat(returnedReviews).hasSize(2);

        verify(reviewService).getReviewsByCustomer(1L);
    }

    @Test
    void getReviewsByProduct_ShouldReturnProductReviewsWithAverageRating() {
        // Arrange
        List<Review> productReviews = Arrays.asList(review1, review2);
        when(reviewService.getReviewsByProduct(1L)).thenReturn(productReviews);
        when(reviewService.getAverageRating(1L)).thenReturn(4.5);

        // Act
        ResponseEntity<Map<String, Object>> response = reviewController.getReviewsByProduct(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("reviews");
        assertThat(response.getBody()).containsKey("averageRating");
        assertThat(response.getBody().get("averageRating")).isEqualTo(4.5);

        @SuppressWarnings("unchecked")
        List<Review> returnedReviews = (List<Review>) response.getBody().get("reviews");
        assertThat(returnedReviews).hasSize(2);

        verify(reviewService).getReviewsByProduct(1L);
        verify(reviewService).getAverageRating(1L);
    }

    @Test
    void getReviewsByProduct_ShouldHandleServiceException() {
        // Arrange
        when(reviewService.getReviewsByProduct(1L))
                .thenThrow(new RuntimeException("Product retrieval error"));

        // Act
        ResponseEntity<Map<String, Object>> response = reviewController.getReviewsByProduct(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message")).asString().contains("Product retrieval error");

        verify(reviewService).getReviewsByProduct(1L);
    }
}

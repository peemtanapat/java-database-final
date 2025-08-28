package com.project.code.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.code.Model.Review;
import com.project.code.Repo.ReviewRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review review1;
    private Review review2;
    private Review review3;

    @BeforeEach
    void setUp() {
        review1 = new Review(1L, 1L, 1L, 5, "Great product!");
        review1.setId("review1");

        review2 = new Review(2L, 1L, 1L, 4, "Good product");
        review2.setId("review2");

        review3 = new Review(1L, 2L, 1L, 3, "Average product");
        review3.setId("review3");
    }

    @Test
    void createReview_ShouldReturnSavedReview() {
        // Arrange
        when(reviewRepository.save(review1)).thenReturn(review1);

        // Act
        Review result = reviewService.createReview(review1);

        // Assert
        assertThat(result).isEqualTo(review1);
        verify(reviewRepository).save(review1);
    }

    @Test
    void getAllReviews_ShouldReturnAllReviews() {
        // Arrange
        List<Review> reviews = Arrays.asList(review1, review2, review3);
        when(reviewRepository.findAll()).thenReturn(reviews);

        // Act
        List<Review> result = reviewService.getAllReviews();

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result).contains(review1, review2, review3);
        verify(reviewRepository).findAll();
    }

    @Test
    void getReviewById_ShouldReturnReviewWhenExists() {
        // Arrange
        when(reviewRepository.findById("review1")).thenReturn(Optional.of(review1));

        // Act
        Optional<Review> result = reviewService.getReviewById("review1");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(review1);
        verify(reviewRepository).findById("review1");
    }

    @Test
    void getReviewById_ShouldReturnEmptyWhenNotExists() {
        // Arrange
        when(reviewRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<Review> result = reviewService.getReviewById("nonexistent");

        // Assert
        assertThat(result).isEmpty();
        verify(reviewRepository).findById("nonexistent");
    }

    @Test
    void getReviewsByStoreAndProduct_ShouldReturnFilteredReviews() {
        // Arrange
        List<Review> storeProductReviews = Arrays.asList(review1, review2);
        when(reviewRepository.findByStoreIdAndProductId(1L, 1L)).thenReturn(storeProductReviews);

        // Act
        List<Review> result = reviewService.getReviewsByStoreAndProduct(1L, 1L);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).contains(review1, review2);
        verify(reviewRepository).findByStoreIdAndProductId(1L, 1L);
    }

    @Test
    void updateReview_ShouldReturnUpdatedReviewWhenExists() {
        // Arrange
        Review updatedReview = new Review(1L, 1L, 1L, 4, "Updated comment");
        when(reviewRepository.existsById("review1")).thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);

        // Act
        Review result = reviewService.updateReview("review1", updatedReview);

        // Assert
        assertThat(result).isEqualTo(updatedReview);
        verify(reviewRepository).existsById("review1");
        verify(reviewRepository).save(updatedReview);
    }

    @Test
    void updateReview_ShouldThrowExceptionWhenNotExists() {
        // Arrange
        Review updatedReview = new Review(1L, 1L, 1L, 4, "Updated comment");
        when(reviewRepository.existsById("nonexistent")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> reviewService.updateReview("nonexistent", updatedReview))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Review not found with ID: nonexistent");

        verify(reviewRepository).existsById("nonexistent");
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void deleteReview_ShouldCallRepositoryDelete() {
        // Act
        reviewService.deleteReview("review1");

        // Assert
        verify(reviewRepository).deleteById("review1");
    }

    @Test
    void getReviewsByCustomer_ShouldReturnFilteredReviews() {
        // Arrange
        List<Review> allReviews = Arrays.asList(review1, review2, review3);
        when(reviewRepository.findAll()).thenReturn(allReviews);

        // Act
        List<Review> result = reviewService.getReviewsByCustomer(1L);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).contains(review1, review3);
        verify(reviewRepository).findAll();
    }

    @Test
    void getReviewsByProduct_ShouldReturnFilteredReviews() {
        // Arrange
        List<Review> allReviews = Arrays.asList(review1, review2, review3);
        when(reviewRepository.findAll()).thenReturn(allReviews);

        // Act
        List<Review> result = reviewService.getReviewsByProduct(1L);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).contains(review1, review2);
        verify(reviewRepository).findAll();
    }

    @Test
    void getAverageRating_ShouldReturnCorrectAverage() {
        // Arrange
        when(reviewRepository.findAll()).thenReturn(Arrays.asList(review1, review2, review3));

        // Act
        double result = reviewService.getAverageRating(1L);

        // Assert
        assertThat(result).isEqualTo(4.5);
        verify(reviewRepository).findAll();
    }

    @Test
    void getAverageRating_ShouldReturnZeroWhenNoReviews() {
        // Arrange
        when(reviewRepository.findAll()).thenReturn(Arrays.asList(review3)); // different product

        // Act
        double result = reviewService.getAverageRating(1L);

        // Assert
        assertThat(result).isEqualTo(0.0);
        verify(reviewRepository).findAll();
    }
}

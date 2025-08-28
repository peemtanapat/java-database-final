package com.project.code.Controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.code.Model.ApiResponse;
import com.project.code.Model.OrderDetails;
import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.PurchaseProductDTO;
import com.project.code.Model.StoreDto;
import com.project.code.Service.OrderService;
import com.project.code.Service.StoreService;

@ExtendWith(MockitoExtension.class)
class StoreControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private StoreService storeService;

    @InjectMocks
    private StoreController storeController;

    private PlaceOrderRequestDTO placeOrderRequest;
    private OrderDetails orderDetails;
    private StoreDto storeDto;

    @BeforeEach
    void setUp() {
        placeOrderRequest = new PlaceOrderRequestDTO(
                1L,
                "John Doe",
                "john@example.com",
                "1234567890",
                LocalDateTime.now().toString(),
                List.of(new PurchaseProductDTO(1L, "Product1", 100.0, 2, 200.0)));

        orderDetails = new OrderDetails();
        orderDetails.setId(1L);

        storeDto = new StoreDto("Test Store", "Test Address");
    }

    @Test
    void placeOrder_shouldReturnSuccessResponse_whenOrderIsPlacedSuccessfully() {
        // Arrange
        when(orderService.saveOrder(any(PlaceOrderRequestDTO.class))).thenReturn(orderDetails);

        // Act
        ApiResponse<OrderDetails> response = storeController.placeOrder(placeOrderRequest);

        // Assert
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getMessage()).isEqualTo("Place order successfully");
        assertThat(response.getData()).isEqualTo(orderDetails);
        verify(orderService).saveOrder(any(PlaceOrderRequestDTO.class));
    }

    @Test
    void addStore_shouldReturnSuccessResponse_whenStoreIsAddedSuccessfully() {
        // Arrange
        when(storeService.addStore(any(StoreDto.class))).thenReturn(true);

        // Act
        ApiResponse<Boolean> response = storeController.addStore(storeDto);

        // Assert
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getMessage()).isEqualTo("add a new store successfully");
        assertThat(response.getData()).isTrue();
        verify(storeService).addStore(any(StoreDto.class));
    }

    @Test
    void validateStore_shouldReturnSuccessResponse_whenStoreExists() {
        // Arrange
        when(storeService.isExistStore(1L)).thenReturn(true);

        // Act
        ApiResponse<Boolean> response = storeController.validateStore(1L);

        // Assert
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getMessage()).isEqualTo("validate store successfully");
        assertThat(response.getData()).isTrue();
        verify(storeService).isExistStore(1L);
    }

    @Test
    void validateStore_shouldReturnSuccessResponse_whenStoreDoesNotExist() {
        // Arrange
        when(storeService.isExistStore(1L)).thenReturn(false);

        // Act
        ApiResponse<Boolean> response = storeController.validateStore(1L);

        // Assert
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getMessage()).isEqualTo("validate store successfully");
        assertThat(response.getData()).isFalse();
        verify(storeService).isExistStore(1L);
    }
}

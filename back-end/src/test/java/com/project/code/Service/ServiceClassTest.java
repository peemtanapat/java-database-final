package com.project.code.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Model.Store;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ServiceClassTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ServiceClass serviceClass;

    private Product product;
    private Store store;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        product = new Product("Test Product", "Test Category", 100.0, "TEST001");
        product.setId(1L);
        store = new Store("Test Store", "Test Address");
        store.setId(1L);
        inventory = new Inventory(store, product, 10);
        inventory.setId(1L);
    }

    @Test
    void validateInventory_shouldReturnTrue_whenInventoryDoesNotExist() {
        // Arrange
        when(inventoryRepository.findByStoreAndProduct(store, product)).thenReturn(Optional.empty());

        // Act
        boolean result = serviceClass.validateInventory(inventory);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void validateInventory_shouldReturnFalse_whenInventoryExists() {
        // Arrange
        when(inventoryRepository.findByStoreAndProduct(store, product)).thenReturn(Optional.of(inventory));

        // Act
        boolean result = serviceClass.validateInventory(inventory);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void validateProduct_shouldReturnTrue_whenProductDoesNotExist() {
        // Arrange
        when(productRepository.findByName("Test Product")).thenReturn(null);

        // Act
        boolean result = serviceClass.validateProduct(product);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void validateProduct_shouldReturnFalse_whenProductExists() {
        // Arrange
        when(productRepository.findByName("Test Product")).thenReturn(product);

        // Act
        boolean result = serviceClass.validateProduct(product);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void ValidateProductId_shouldReturnTrue_whenProductExists() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = serviceClass.ValidateProductId(1L);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void ValidateProductId_shouldReturnFalse_whenProductDoesNotExist() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(false);

        // Act
        boolean result = serviceClass.ValidateProductId(1L);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void getInventoryId_shouldReturnInventory_whenExists() {
        // Arrange
        when(inventoryRepository.findByStoreAndProduct(store, product)).thenReturn(Optional.of(inventory));

        // Act
        Inventory result = serviceClass.getInventoryId(inventory);

        // Assert
        assertThat(result).isEqualTo(inventory);
    }

    @Test
    void getInventoryId_shouldReturnNull_whenDoesNotExist() {
        // Arrange
        when(inventoryRepository.findByStoreAndProduct(store, product)).thenReturn(Optional.empty());

        // Act
        Inventory result = serviceClass.getInventoryId(inventory);

        // Assert
        assertThat(result).isNull();
    }
}

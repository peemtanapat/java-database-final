package com.project.code.Controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Model.Store;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ServiceClass serviceClass;

    @InjectMocks
    private InventoryController inventoryController;

    private Product product;
    private Store store;
    private Inventory inventory;
    private CombinedRequest combinedRequest;

    @BeforeEach
    void setUp() {
        product = new Product("Test Product", "Test Category", 100.0, "TEST001");
        product.setId(1L);

        store = new Store("Test Store", "Test Address");
        store.setId(1L);

        inventory = new Inventory(store, product, 10);
        inventory.setId(1L);

        combinedRequest = new CombinedRequest();
        combinedRequest.setProduct(product);
        combinedRequest.setInventory(inventory);
    }

    @Test
    void updateInventory_shouldReturnOkResponse_whenInventoryIsUpdatedSuccessfully() {
        // Arrange
        when(serviceClass.ValidateProductId(1L)).thenReturn(true);
        when(serviceClass.getInventoryId(inventory)).thenReturn(inventory);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.updateInventory(combinedRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("message")).isEqualTo("Inventory updated successfully");
        verify(serviceClass).ValidateProductId(1L);
        verify(serviceClass).getInventoryId(inventory);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void updateInventory_shouldReturnNotFound_whenProductDoesNotExist() {
        // Arrange
        when(serviceClass.ValidateProductId(1L)).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.updateInventory(combinedRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().get("message")).isEqualTo("Product not found with ID: 1");
        verify(serviceClass).ValidateProductId(1L);
    }

    @Test
    void updateInventory_shouldReturnOkResponse_whenNoInventoryExists() {
        // Arrange
        when(serviceClass.ValidateProductId(1L)).thenReturn(true);
        when(serviceClass.getInventoryId(inventory)).thenReturn(null);

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.updateInventory(combinedRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("message"))
                .isEqualTo("No inventory data available for this product and store combination");
        verify(serviceClass).ValidateProductId(1L);
        verify(serviceClass).getInventoryId(inventory);
    }

    @Test
    void updateInventory_shouldReturnInternalServerError_whenExceptionOccurs() {
        // Arrange
        when(serviceClass.ValidateProductId(1L)).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.updateInventory(combinedRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().get("message")).isEqualTo("Error updating inventory: Database error");
        verify(serviceClass).ValidateProductId(1L);
    }

    @Test
    void saveInventory_shouldReturnCreatedResponse_whenInventoryIsSavedSuccessfully() {
        // Arrange
        Inventory newInventory = new Inventory(store, product, 5);
        when(serviceClass.validateInventory(newInventory)).thenReturn(true);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(newInventory);

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.saveInventory(newInventory);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("message")).isEqualTo("Inventory saved successfully");
        assertThat(response.getBody()).containsKey("inventory");
        verify(serviceClass).validateInventory(newInventory);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void saveInventory_shouldReturnConflict_whenInventoryAlreadyExists() {
        // Arrange
        Inventory newInventory = new Inventory(store, product, 5);
        when(serviceClass.validateInventory(newInventory)).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.saveInventory(newInventory);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().get("message"))
                .isEqualTo("Inventory already exists for this product and store combination");
        verify(serviceClass).validateInventory(newInventory);
    }

    @Test
    void saveInventory_shouldReturnInternalServerError_whenExceptionOccurs() {
        // Arrange
        Inventory newInventory = new Inventory(store, product, 5);
        when(serviceClass.validateInventory(newInventory)).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.saveInventory(newInventory);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().get("message")).isEqualTo("Error saving inventory: Database error");
        verify(serviceClass).validateInventory(newInventory);
    }

    @Test
    void getAllProducts_shouldReturnOkResponse_withProducts() {
        // Arrange
        List<Inventory> inventories = List.of(inventory);
        when(inventoryRepository.findByStoreId(1L)).thenReturn(inventories);

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.getAllProducts(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("products");
        verify(inventoryRepository).findByStoreId(1L);
    }

    @Test
    void getAllProducts_shouldReturnInternalServerError_whenExceptionOccurs() {
        // Arrange
        when(inventoryRepository.findByStoreId(1L)).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.getAllProducts(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().get("message")).isEqualTo("Error retrieving products: Database error");
        verify(inventoryRepository).findByStoreId(1L);
    }

    @Test
    void getProductName_shouldReturnAllProducts_whenBothCategoryAndNameAreNull() {
        // Arrange
        List<Product> products = List.of(product);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.getProductName("null", "null");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("product");
        verify(productRepository).findAll();
    }

    @Test
    void getProductName_shouldReturnProductsByName_whenCategoryIsNull() {
        // Arrange
        List<Product> products = List.of(product);
        when(productRepository.findProductBySubName("Test")).thenReturn(products);

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.getProductName("null", "Test");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("product");
        verify(productRepository).findProductBySubName("Test");
    }

    @Test
    void getProductName_shouldReturnProductsByCategory_whenNameIsNull() {
        // Arrange
        List<Product> products = List.of(product);
        when(productRepository.findByCategory("Test Category")).thenReturn(products);

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.getProductName("Test Category", "null");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("product");
        verify(productRepository).findByCategory("Test Category");
    }

    @Test
    void getProductName_shouldReturnProductsByNameAndCategory_whenBothAreProvided() {
        // Arrange
        List<Product> products = List.of(product);
        when(productRepository.findProductBySubNameAndCategory("Test", "Test Category")).thenReturn(products);

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.getProductName("Test Category", "Test");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("product");
        verify(productRepository).findProductBySubNameAndCategory("Test", "Test Category");
    }

    @Test
    void getProductName_shouldReturnInternalServerError_whenExceptionOccurs() {
        // Arrange
        when(productRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.getProductName("null", "null");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().get("message")).isEqualTo("Error filtering products: Database error");
        verify(productRepository).findAll();
    }

    @Test
    void searchProduct_shouldReturnOkResponse_withProducts() {
        // Arrange
        List<Product> products = List.of(product);
        when(productRepository.findByNameLike(1L, "Test")).thenReturn(products);

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.searchProduct("Test", 1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("product");
        verify(productRepository).findByNameLike(1L, "Test");
    }

    @Test
    void searchProduct_shouldReturnInternalServerError_whenExceptionOccurs() {
        // Arrange
        when(productRepository.findByNameLike(1L, "Test")).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.searchProduct("Test", 1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().get("message")).isEqualTo("Error searching products: Database error");
        verify(productRepository).findByNameLike(1L, "Test");
    }

    @Test
    void removeProduct_shouldReturnOkResponse_whenProductIsDeletedSuccessfully() {
        // Arrange
        when(serviceClass.ValidateProductId(1L)).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.removeProduct(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("message")).isEqualTo("Product and related inventory deleted successfully");
        verify(serviceClass).ValidateProductId(1L);
        verify(inventoryRepository).deleteByProductId(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void removeProduct_shouldReturnNotFound_whenProductDoesNotExist() {
        // Arrange
        when(serviceClass.ValidateProductId(1L)).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.removeProduct(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().get("message")).isEqualTo("Product not found with ID: 1");
        verify(serviceClass).ValidateProductId(1L);
    }

    @Test
    void removeProduct_shouldReturnInternalServerError_whenExceptionOccurs() {
        // Arrange
        when(serviceClass.ValidateProductId(1L)).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.removeProduct(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().get("message")).isEqualTo("Error deleting product: Database error");
        verify(serviceClass).ValidateProductId(1L);
    }

    @Test
    void validateQuantity_shouldReturnOkResponse_whenQuantityIsAvailable() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByStoreAndProduct(any(Store.class), eq(product)))
                .thenReturn(Optional.of(inventory));

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.validateQuantity(1L, 1L, 5);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("available")).isEqualTo(true);
        assertThat(response.getBody().get("requestedQuantity")).isEqualTo(5);
        assertThat(response.getBody().get("availableStock")).isEqualTo(10);
        verify(productRepository).findById(1L);
        verify(inventoryRepository).findByStoreAndProduct(any(Store.class), eq(product));
    }

    @Test
    void validateQuantity_shouldReturnOkResponse_whenQuantityIsNotAvailable() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByStoreAndProduct(any(Store.class), eq(product)))
                .thenReturn(Optional.of(inventory));

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.validateQuantity(1L, 1L, 15);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("available")).isEqualTo(false);
        assertThat(response.getBody().get("requestedQuantity")).isEqualTo(15);
        assertThat(response.getBody().get("availableStock")).isEqualTo(10);
        verify(productRepository).findById(1L);
        verify(inventoryRepository).findByStoreAndProduct(any(Store.class), eq(product));
    }

    @Test
    void validateQuantity_shouldReturnOkResponse_whenNoInventoryExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByStoreAndProduct(any(Store.class), eq(product))).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.validateQuantity(1L, 1L, 5);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("available")).isEqualTo(false);
        assertThat(response.getBody().get("requestedQuantity")).isEqualTo(5);
        assertThat(response.getBody().get("availableStock")).isEqualTo(0);
        verify(productRepository).findById(1L);
        verify(inventoryRepository).findByStoreAndProduct(any(Store.class), eq(product));
    }

    @Test
    void validateQuantity_shouldReturnNotFound_whenProductDoesNotExist() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.validateQuantity(1L, 1L, 5);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().get("message")).isEqualTo("Product not found with ID: 1");
        assertThat(response.getBody().get("available")).isEqualTo(false);
        verify(productRepository).findById(1L);
    }

    @Test
    void validateQuantity_shouldReturnInternalServerError_whenExceptionOccurs() {
        // Arrange
        when(productRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Map<String, Object>> response = inventoryController.validateQuantity(1L, 1L, 5);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().get("message")).isEqualTo("Database error");
        assertThat(response.getBody().get("available")).isEqualTo(false);
        verify(productRepository).findById(1L);
    }
}

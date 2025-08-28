package com.project.code.Controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.project.code.Model.Product;
import com.project.code.Model.ProductDto;
import com.project.code.Service.ProductService;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        product = new Product("Test Product", "Test Category", 100.0, "TEST001");
        product.setId(1L);
        productDto = new ProductDto(1L, "Test Product", "Test Category", 100.0, "TEST001");
    }

    @Test
    void addProduct_shouldReturnCreatedResponse_whenProductIsAddedSuccessfully() {
        // Arrange
        Product inputProduct = new Product("Test Product", "Test Category", 100.0, "TEST001");
        when(productService.addProduct(any(ProductDto.class))).thenReturn(productDto);

        // Act
        ResponseEntity<Map<String, Object>> response = productController.addProduct(inputProduct);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsKey("product");
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message")).isEqualTo("Product added successfully");
        verify(productService).addProduct(any(ProductDto.class));
    }

    @Test
    void addProduct_shouldReturnBadRequest_whenDataIntegrityViolationOccurs() {
        // Arrange
        Product inputProduct = new Product("Test Product", "Test Category", 100.0, "TEST001");
        when(productService.addProduct(any(ProductDto.class)))
                .thenThrow(new DataIntegrityViolationException("Constraint violation"));

        // Act
        ResponseEntity<Map<String, Object>> response = productController.addProduct(inputProduct);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message")).isEqualTo("Product with this SKU already exists");
        verify(productService).addProduct(any(ProductDto.class));
    }

    @Test
    void addProduct_shouldReturnBadRequest_whenRuntimeExceptionOccurs() {
        // Arrange
        Product inputProduct = new Product("Test Product", "Test Category", 100.0, "TEST001");
        when(productService.addProduct(any(ProductDto.class)))
                .thenThrow(new RuntimeException("Product already exists"));

        // Act
        ResponseEntity<Map<String, Object>> response = productController.addProduct(inputProduct);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message")).isEqualTo("Product already exists");
        verify(productService).addProduct(any(ProductDto.class));
    }

    @Test
    void getProductbyId_shouldReturnProduct_whenProductExists() {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(productDto);

        // Act
        ResponseEntity<Map<String, Object>> response = productController.getProductbyId(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("products");
        verify(productService).getProductById(1L);
    }

    @Test
    void getProductbyId_shouldReturnNotFound_whenProductDoesNotExist() {
        // Arrange
        when(productService.getProductById(1L)).thenThrow(new RuntimeException("Product not found"));

        // Act
        ResponseEntity<Map<String, Object>> response = productController.getProductbyId(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().get("message")).isEqualTo("Product not found");
        verify(productService).getProductById(1L);
    }

    @Test
    void updateProduct_shouldReturnOkResponse_whenProductIsUpdatedSuccessfully() {
        // Arrange
        Product inputProduct = new Product("Updated Product", "Updated Category", 150.0, "UPDATED001");
        when(productService.updateProduct(eq(1L), any(ProductDto.class))).thenReturn(productDto);

        // Act
        ResponseEntity<Map<String, Object>> response = productController.updateProduct(1L, inputProduct);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody()).containsKey("product");
        assertThat(response.getBody().get("message")).isEqualTo("Product updated successfully");
        verify(productService).updateProduct(eq(1L), any(ProductDto.class));
    }

    @Test
    void updateProduct_shouldReturnBadRequest_whenRuntimeExceptionOccurs() {
        // Arrange
        Product inputProduct = new Product("Updated Product", "Updated Category", 150.0, "UPDATED001");
        when(productService.updateProduct(eq(1L), any(ProductDto.class)))
                .thenThrow(new RuntimeException("Product not found"));

        // Act
        ResponseEntity<Map<String, Object>> response = productController.updateProduct(1L, inputProduct);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message")).isEqualTo("Product not found");
        verify(productService).updateProduct(eq(1L), any(ProductDto.class));
    }

    @Test
    void filterbyCategoryProduct_shouldReturnProducts_whenFilteringByCategory() {
        // Arrange
        List<ProductDto> productDtos = List.of(productDto);
        when(productService.filterByCategoryProduct("Test Category")).thenReturn(productDtos);

        // Act
        ResponseEntity<Map<String, Object>> response = productController.filterbyCategoryProduct("null",
                "Test Category");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("products");
        verify(productService).filterByCategoryProduct("Test Category");
    }

    @Test
    void filterbyCategoryProduct_shouldReturnProducts_whenSearchingByName() {
        // Arrange
        List<ProductDto> productDtos = List.of(productDto);
        when(productService.searchProduct("Test")).thenReturn(productDtos);

        // Act
        ResponseEntity<Map<String, Object>> response = productController.filterbyCategoryProduct("Test", "null");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("products");
        verify(productService).searchProduct("Test");
    }

    @Test
    void filterbyCategoryProduct_shouldReturnAllProducts_whenBothParametersAreNull() {
        // Arrange
        List<ProductDto> productDtos = List.of(productDto);
        when(productService.listProduct()).thenReturn(productDtos);

        // Act
        ResponseEntity<Map<String, Object>> response = productController.filterbyCategoryProduct("null", "null");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("products");
        verify(productService).listProduct();
    }

    @Test
    void listProduct_shouldReturnAllProducts() {
        // Arrange
        List<ProductDto> productDtos = List.of(productDto);
        when(productService.listProduct()).thenReturn(productDtos);

        // Act
        ResponseEntity<Map<String, Object>> response = productController.listProduct();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("products");
        verify(productService).listProduct();
    }

    @Test
    void getProductbyCategoryAndStoreId_shouldReturnFilteredProducts() {
        // Arrange
        List<ProductDto> productDtos = List.of(productDto);
        when(productService.getProductByCategoryAndStoreId("Test Category", 1L)).thenReturn(productDtos);

        // Act
        ResponseEntity<Map<String, Object>> response = productController.getProductbyCategoryAndStoreId("Test Category",
                1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("product");
        verify(productService).getProductByCategoryAndStoreId("Test Category", 1L);
    }

    @Test
    void deleteProduct_shouldReturnOkResponse_whenProductIsDeletedSuccessfully() {
        // Act
        ResponseEntity<Map<String, Object>> response = productController.deleteProduct(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message")).isEqualTo("Product deleted successfully");
        verify(productService).deleteProduct(1L);
    }

    @Test
    void deleteProduct_shouldReturnBadRequest_whenRuntimeExceptionOccurs() {
        // Arrange
        doThrow(new RuntimeException("Product not found")).when(productService).deleteProduct(1L);

        // Act
        ResponseEntity<Map<String, Object>> response = productController.deleteProduct(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message")).isEqualTo("Product not found");
        verify(productService).deleteProduct(1L);
    }

    @Test
    void searchProduct_shouldReturnMatchingProducts() {
        // Arrange
        List<ProductDto> productDtos = List.of(productDto);
        when(productService.searchProduct("Test")).thenReturn(productDtos);

        // Act
        ResponseEntity<Map<String, Object>> response = productController.searchProduct("Test");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("products");
        verify(productService).searchProduct("Test");
    }
}

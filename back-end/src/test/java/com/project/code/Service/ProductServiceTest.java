package com.project.code.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Model.ProductDto;
import com.project.code.Model.Store;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ServiceClass serviceClass;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        product = new Product("Test Product", "Test Category", 100.0, "TEST001");
        product.setId(1L);
        productDto = new ProductDto(1L, "Test Product", "Test Category", 100.0, "TEST001");
    }

    @Test
    void addProduct_shouldReturnProductDto_whenProductIsValid() {
        // Arrange
        ProductDto inputDto = new ProductDto(null, "Test Product", "Test Category", 100.0, "TEST001");
        when(serviceClass.validateProduct(any(Product.class))).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductDto result = productService.addProduct(inputDto);

        // Assert
        assertThat(result).isEqualTo(productDto);
        verify(serviceClass).validateProduct(any(Product.class));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void addProduct_shouldThrowException_whenProductAlreadyExists() {
        // Arrange
        ProductDto inputDto = new ProductDto(null, "Test Product", "Test Category", 100.0, "TEST001");
        when(serviceClass.validateProduct(any(Product.class))).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> productService.addProduct(inputDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product with name 'Test Product' already exists");

        verify(serviceClass).validateProduct(any(Product.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductById_shouldReturnProductDto_whenProductExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ProductDto result = productService.getProductById(1L);

        // Assert
        assertThat(result).isEqualTo(productDto);
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_shouldThrowException_whenProductNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.getProductById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product not found with ID: 1");

        verify(productRepository).findById(1L);
    }

    @Test
    void updateProduct_shouldReturnUpdatedProductDto_whenProductExists() {
        // Arrange
        ProductDto updateDto = new ProductDto(1L, "Updated Product", "Updated Category", 150.0, "UPDATED001");
        Product updatedProduct = new Product("Updated Product", "Updated Category", 150.0, "UPDATED001");
        updatedProduct.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        ProductDto result = productService.updateProduct(1L, updateDto);

        // Assert
        assertThat(result.name()).isEqualTo("Updated Product");
        assertThat(result.category()).isEqualTo("Updated Category");
        assertThat(result.price()).isEqualTo(150.0);
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_shouldThrowException_whenProductNotFound() {
        // Arrange
        ProductDto updateDto = new ProductDto(1L, "Updated Product", "Updated Category", 150.0, "UPDATED001");
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.updateProduct(1L, updateDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product not found with ID: 1");

        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void filterByCategoryProduct_shouldReturnListOfProductDto() {
        // Arrange
        List<Product> products = List.of(product);
        when(productRepository.findByCategory("Test Category")).thenReturn(products);

        // Act
        List<ProductDto> result = productService.filterByCategoryProduct("Test Category");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(productDto);
        verify(productRepository).findByCategory("Test Category");
    }

    @Test
    void listProduct_shouldReturnAllProductsAsDto() {
        // Arrange
        List<Product> products = List.of(product);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<ProductDto> result = productService.listProduct();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(productDto);
        verify(productRepository).findAll();
    }

    @Test
    void getProductByCategoryAndStoreId_shouldReturnFilteredProducts() {
        // Arrange
        Store store = new Store("Test Store", "Test Address");
        store.setId(1L);
        Inventory inventory = new Inventory(store, product, 10);
        product.setInventories(List.of(inventory));

        List<Product> products = List.of(product);
        when(productRepository.findProductByCategory("Test Category")).thenReturn(products);

        // Act
        List<ProductDto> result = productService.getProductByCategoryAndStoreId("Test Category", 1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(productDto);
        verify(productRepository).findProductByCategory("Test Category");
    }

    @Test
    void deleteProduct_shouldDeleteProduct_whenProductExists() {
        // Arrange
        when(serviceClass.ValidateProductId(1L)).thenReturn(true);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(serviceClass).ValidateProductId(1L);
        verify(inventoryRepository).deleteByProductId(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_shouldThrowException_whenProductNotFound() {
        // Arrange
        when(serviceClass.ValidateProductId(1L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> productService.deleteProduct(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product not found with ID: 1");

        verify(serviceClass).ValidateProductId(1L);
        verify(inventoryRepository, never()).deleteByProductId(1L);
        verify(productRepository, never()).deleteById(1L);
    }

    @Test
    void searchProduct_shouldReturnMatchingProducts() {
        // Arrange
        List<Product> products = List.of(product);
        when(productRepository.findProductBySubName("Test")).thenReturn(products);

        // Act
        List<ProductDto> result = productService.searchProduct("Test");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(productDto);
        verify(productRepository).findProductBySubName("Test");
    }
}

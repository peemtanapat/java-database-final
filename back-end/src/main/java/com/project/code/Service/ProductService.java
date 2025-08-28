package com.project.code.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.code.Model.Product;
import com.project.code.Model.ProductDto;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ServiceClass serviceClass;

    public ProductDto addProduct(ProductDto productDto) {
        log.info("Adding new product: {}", productDto.name());

        // Convert DTO to entity
        Product product = new Product(
                productDto.name(),
                productDto.category(),
                productDto.price(),
                productDto.sku());

        // Validate product doesn't already exist
        if (!serviceClass.validateProduct(product)) {
            throw new RuntimeException("Product with name '" + productDto.name() + "' already exists");
        }

        // Save the product
        Product savedProduct = productRepository.save(product);

        // Convert back to DTO
        return new ProductDto(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getCategory(),
                savedProduct.getPrice(),
                savedProduct.getSku());
    }

    public ProductDto getProductById(Long id) {
        log.info("Getting product by ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getSku());
    }

    public ProductDto updateProduct(Long id, ProductDto productDto) {
        log.info("Updating product with ID: {}", id);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        // Update fields
        existingProduct.setName(productDto.name());
        existingProduct.setCategory(productDto.category());
        existingProduct.setPrice(productDto.price());
        existingProduct.setSku(productDto.sku());

        // Save updated product
        Product updatedProduct = productRepository.save(existingProduct);

        return new ProductDto(
                updatedProduct.getId(),
                updatedProduct.getName(),
                updatedProduct.getCategory(),
                updatedProduct.getPrice(),
                updatedProduct.getSku());
    }

    public List<ProductDto> filterByCategoryProduct(String category) {
        log.info("Filtering products by category: {}", category);

        List<Product> products = productRepository.findByCategory(category);

        return products.stream()
                .map(product -> new ProductDto(
                        product.getId(),
                        product.getName(),
                        product.getCategory(),
                        product.getPrice(),
                        product.getSku()))
                .collect(Collectors.toList());
    }

    public List<ProductDto> listProduct() {
        log.info("Listing all products");

        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> new ProductDto(
                        product.getId(),
                        product.getName(),
                        product.getCategory(),
                        product.getPrice(),
                        product.getSku()))
                .collect(Collectors.toList());
    }

    public List<ProductDto> getProductByCategoryAndStoreId(String category, Long storeId) {
        log.info("Getting products by category: {} and store ID: {}", category, storeId);

        List<Product> products = productRepository.findProductByCategory(category);

        // Filter by store availability
        return products.stream()
                .filter(product -> product.getInventories().stream()
                        .anyMatch(inventory -> inventory.getStore().getId() == storeId))
                .map(product -> new ProductDto(
                        product.getId(),
                        product.getName(),
                        product.getCategory(),
                        product.getPrice(),
                        product.getSku()))
                .collect(Collectors.toList());
    }

    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);

        // Validate product exists
        if (!serviceClass.ValidateProductId(id)) {
            throw new RuntimeException("Product not found with ID: " + id);
        }

        // Delete inventory records first
        inventoryRepository.deleteByProductId(id);

        // Delete the product
        productRepository.deleteById(id);

        log.info("Product deleted successfully: {}", id);
    }

    public List<ProductDto> searchProduct(String name) {
        log.info("Searching products by name: {}", name);

        List<Product> products = productRepository.findProductBySubName(name);

        return products.stream()
                .map(product -> new ProductDto(
                        product.getId(),
                        product.getName(),
                        product.getCategory(),
                        product.getPrice(),
                        product.getSku()))
                .collect(Collectors.toList());
    }
}

package com.project.code.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.Product;
import com.project.code.Model.ProductDto;
import com.project.code.Service.ProductService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> addProduct(@RequestBody Product product) {
        try {
            // Convert Product to ProductDto for service
            ProductDto productDto = new ProductDto(
                    null, // id will be generated
                    product.getName(),
                    product.getCategory(),
                    product.getPrice(),
                    product.getSku());

            ProductDto savedProduct = productService.addProduct(productDto);

            // Convert back to Product entity for response
            Product responseProduct = new Product(
                    savedProduct.name(),
                    savedProduct.category(),
                    savedProduct.price(),
                    savedProduct.sku());
            responseProduct.setId(savedProduct.id());

            Map<String, Object> response = new HashMap<>();
            response.put("product", responseProduct);
            response.put("message", "Product added successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (DataIntegrityViolationException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Product with this SKU already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductbyId(@PathVariable Long id) {
        try {
            ProductDto productDto = productService.getProductById(id);

            Product product = new Product(
                    productDto.name(),
                    productDto.category(),
                    productDto.price(),
                    productDto.sku());
            product.setId(productDto.id());

            Map<String, Object> response = new HashMap<>();
            response.put("products", product);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            ProductDto productDto = new ProductDto(
                    id,
                    product.getName(),
                    product.getCategory(),
                    product.getPrice(),
                    product.getSku());

            ProductDto updatedProduct = productService.updateProduct(id, productDto);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Product updated successfully");
            response.put("product", updatedProduct);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/category/{name}/{category}")
    public ResponseEntity<Map<String, Object>> filterbyCategoryProduct(
            @PathVariable String name,
            @PathVariable String category) {

        List<ProductDto> products;

        if ("null".equals(name) && "null".equals(category)) {
            products = productService.listProduct();
        } else if ("null".equals(name)) {
            products = productService.filterByCategoryProduct(category);
        } else if ("null".equals(category)) {
            products = productService.searchProduct(name);
        } else {
            // For combined name and category search, we might need a new service method
            // For now, let's use category filter
            products = productService.filterByCategoryProduct(category);
        }

        List<Product> productEntities = products.stream()
                .map(dto -> {
                    Product p = new Product(dto.name(), dto.category(), dto.price(), dto.sku());
                    p.setId(dto.id());
                    return p;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("products", productEntities);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listProduct() {
        List<ProductDto> products = productService.listProduct();

        List<Product> productEntities = products.stream()
                .map(dto -> {
                    Product p = new Product(dto.name(), dto.category(), dto.price(), dto.sku());
                    p.setId(dto.id());
                    return p;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("products", productEntities);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/{category}/{storeid}")
    public ResponseEntity<Map<String, Object>> getProductbyCategoryAndStoreId(
            @PathVariable String category,
            @PathVariable Long storeid) {

        List<ProductDto> products = productService.getProductByCategoryAndStoreId(category, storeid);

        List<Product> productEntities = products.stream()
                .map(dto -> {
                    Product p = new Product(dto.name(), dto.category(), dto.price(), dto.sku());
                    p.setId(dto.id());
                    return p;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("product", productEntities);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Product deleted successfully");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/searchProduct/{name}")
    public ResponseEntity<Map<String, Object>> searchProduct(@PathVariable String name) {
        List<ProductDto> products = productService.searchProduct(name);

        List<Product> productEntities = products.stream()
                .map(dto -> {
                    Product p = new Product(dto.name(), dto.category(), dto.price(), dto.sku());
                    p.setId(dto.id());
                    return p;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("products", productEntities);

        return ResponseEntity.ok(response);
    }
}

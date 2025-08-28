package com.project.code.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ServiceClass serviceClass;

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateInventory(@RequestBody CombinedRequest combinedRequest) {
        try {
            Product product = combinedRequest.getProduct();
            Inventory inventory = combinedRequest.getInventory();

            // Validate product ID
            if (!serviceClass.ValidateProductId(product.getId())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Product not found with ID: " + product.getId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Check if inventory exists
            Inventory existingInventory = serviceClass.getInventoryId(inventory);

            Map<String, Object> response = new HashMap<>();
            if (existingInventory != null) {
                // Update existing inventory
                existingInventory.setStockLevel(inventory.getStockLevel());
                inventoryRepository.save(existingInventory);
                response.put("message", "Inventory updated successfully");
            } else {
                response.put("message", "No inventory data available for this product and store combination");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error updating inventory: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveInventory(@RequestBody Inventory inventory) {
        try {
            // Validate if inventory already exists
            boolean inventoryExists = !serviceClass.validateInventory(inventory);

            Map<String, Object> response = new HashMap<>();
            if (inventoryExists) {
                response.put("message", "Inventory already exists for this product and store combination");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // Save new inventory
            Inventory savedInventory = inventoryRepository.save(inventory);
            response.put("message", "Inventory saved successfully");
            response.put("inventory", savedInventory);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error saving inventory: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<Map<String, Object>> getAllProducts(@PathVariable Long storeId) {
        try {
            List<Inventory> inventories = inventoryRepository.findByStoreId(storeId);

            List<Product> products = inventories.stream()
                    .map(Inventory::getProduct)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("products", products);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error retrieving products: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/filter/{category}/{name}")
    public ResponseEntity<Map<String, Object>> getProductName(
            @PathVariable String category,
            @PathVariable String name) {

        try {
            List<Product> products;

            if ("null".equals(category) && "null".equals(name)) {
                products = productRepository.findAll();
            } else if ("null".equals(category)) {
                products = productRepository.findProductBySubName(name);
            } else if ("null".equals(name)) {
                products = productRepository.findByCategory(category);
            } else {
                products = productRepository.findProductBySubNameAndCategory(name, category);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("product", products);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error filtering products: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProduct(
            @RequestParam String name,
            @RequestParam Long storeId) {

        try {
            List<Product> products = productRepository.findByNameLike(storeId, name);

            Map<String, Object> response = new HashMap<>();
            response.put("product", products);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error searching products: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> removeProduct(@PathVariable Long id) {
        try {
            // Validate product exists
            if (!serviceClass.ValidateProductId(id)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Product not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Delete inventory records first
            inventoryRepository.deleteByProductId(id);

            // Delete the product
            productRepository.deleteById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Product and related inventory deleted successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error deleting product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateQuantity(
            @RequestParam Long productId,
            @RequestParam Long storeId,
            @RequestParam Integer quantity) {

        try {
            // Get the product
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

            // Get the store - we need to create a store object or find it
            // For now, let's assume we can create a store with the ID
            com.project.code.Model.Store store = new com.project.code.Model.Store();
            store.setId(storeId);

            // Find inventory for this product and store
            Inventory inventory = inventoryRepository.findByStoreAndProduct(store, product)
                    .orElse(null);

            boolean isAvailable = false;
            if (inventory != null) {
                isAvailable = inventory.getStockLevel() >= quantity;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("available", isAvailable);
            response.put("requestedQuantity", quantity);
            if (inventory != null) {
                response.put("availableStock", inventory.getStockLevel());
            } else {
                response.put("availableStock", 0);
            }

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("available", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error validating quantity: " + e.getMessage());
            errorResponse.put("available", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

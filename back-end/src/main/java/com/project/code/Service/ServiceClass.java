package com.project.code.Service;

import org.springframework.stereotype.Service;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ServiceClass {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public boolean validateInventory(Inventory inventory) {
        return inventoryRepository.findByStoreAndProduct(inventory.getStore(), inventory.getProduct()).isEmpty();
    }

    public boolean validateProduct(Product product) {
        return productRepository.findByName(product.getName()) == null;
    }

    public boolean ValidateProductId(long id) {
        return productRepository.existsById(id);
    }

    public Inventory getInventoryId(Inventory inventory) {
        return inventoryRepository.findByStoreAndProduct(inventory.getStore(), inventory.getProduct()).orElse(null);
    }

}

package com.project.code.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryTest {

    private Store store;
    private Product product;

    @BeforeEach
    void setUp() {
        store = new Store("Test Store", "123 Test Street");
        store.setId(1L);

        product = new Product("Test Product", "Test Category", 100.0, "TEST001");
        product.setId(1L);
    }

    @Test
    void constructor_ShouldAcceptNonNegativeStockLevel() {
        // Act
        try (Inventory inventory = new Inventory(store, product, 10)) {
            // Assert
            assertThat(inventory.getStockLevel()).isEqualTo(10);
            assertThat(inventory.getStore()).isEqualTo(store);
            assertThat(inventory.getProduct()).isEqualTo(product);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void constructor_ShouldAcceptZeroStockLevel() {
        // Act
        try (Inventory inventory = new Inventory(store, product, 0)) {
            // Assert
            assertThat(inventory.getStockLevel()).isEqualTo(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void constructor_ShouldThrowExceptionForNegativeStockLevel() {
        // Act & Assert
        assertThatThrownBy(() -> new Inventory(store, product, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Stock level cannot be negative");
    }

    @Test
    void setStockLevel_ShouldAcceptNonNegativeValue() {
        // Arrange & Act
        try (Inventory inventory = new Inventory(store, product, 5)) {
            // Act
            inventory.setStockLevel(15);

            // Assert
            assertThat(inventory.getStockLevel()).isEqualTo(15);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void setStockLevel_ShouldAcceptZeroValue() {
        // Arrange & Act
        try (Inventory inventory = new Inventory(store, product, 5)) {
            // Act
            inventory.setStockLevel(0);

            // Assert
            assertThat(inventory.getStockLevel()).isEqualTo(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void setStockLevel_ShouldThrowExceptionForNegativeValue() {
        // Arrange & Act
        try (Inventory inventory = new Inventory(store, product, 5)) {
            // Act & Assert
            assertThatThrownBy(() -> inventory.setStockLevel(-1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Stock level cannot be negative");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyInventory() {
        // Act
        try (Inventory inventory = new Inventory()) {
            // Assert
            assertThat(inventory.getId()).isEqualTo(0L);
            assertThat(inventory.getStockLevel()).isEqualTo(0);
            assertThat(inventory.getStore()).isNull();
            assertThat(inventory.getProduct()).isNull();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange & Act
        try (Inventory inventory = new Inventory()) {
            // Act
            inventory.setId(100L);
            inventory.setStore(store);
            inventory.setProduct(product);
            inventory.setStockLevel(25);

            // Assert
            assertThat(inventory.getId()).isEqualTo(100L);
            assertThat(inventory.getStore()).isEqualTo(store);
            assertThat(inventory.getProduct()).isEqualTo(product);
            assertThat(inventory.getStockLevel()).isEqualTo(25);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

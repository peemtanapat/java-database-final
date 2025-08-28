package com.project.code.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.code.Model.Customer;
import com.project.code.Model.Inventory;
import com.project.code.Model.OrderDetails;
import com.project.code.Model.OrderItem;
import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Product;
import com.project.code.Model.PurchaseProductDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderDetailsRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Repo.StoreRepository;
import com.project.code.exception.InventoryStockInsufficientException;
import com.project.code.exception.ProductNotFoundException;
import com.project.code.exception.StoreNotFoundException;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private StoreRepository storeRepository;

    private Customer customer1;
    private Store store1;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setup() {
        orderItemRepository.deleteAll();
        orderDetailsRepository.deleteAll();
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
        storeRepository.deleteAll();
        customerRepository.deleteAll();

        customer1 = customerRepository.save(new Customer("Customer One", "customer1@mail.com", "0939451111"));
        // create a store
        store1 = storeRepository.save(new Store("Store1", "111/1 Bangkok"));
        // create products
        product1 = productRepository.save(new Product("Product1", "Category1", 250.00, "SKU001"));
        product2 = productRepository.save(new Product("Product1", "Category1", 300.00, "SKU002"));
        // create inventory of each product
        inventoryRepository.save(new Inventory(store1, product1, 2));
        inventoryRepository.save(new Inventory(store1, product2, 1));
    }

    @Test
    void saveOrder_whenStockIsSufficient() {
        // Arrange
        PlaceOrderRequestDTO placeOrderRequest = new PlaceOrderRequestDTO(
                store1.getId(),
                customer1.getName(),
                customer1.getEmail(),
                customer1.getPhone(),
                LocalDateTime.now().toString(),
                List.of(
                        new PurchaseProductDTO(product1.getId(), product1.getName(), product1.getPrice(), 2, 500.00),
                        new PurchaseProductDTO(product2.getId(), product2.getName(), product2.getPrice(), 1, 300.00)));
        // Action
        orderService.saveOrder(placeOrderRequest);
        // Assert
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        OrderDetails orderDetails = orderDetailsList.get(0);
        assertThat(orderDetails.getCustomer().getEmail()).isEqualTo(customer1.getEmail());
        assertThat(orderDetails.getStore().getName()).isEqualTo(store1.getName());
        assertThat(orderDetails.getTotalPrice()).isEqualTo(800.00);
        assertThat(orderDetails.getOrderItems().size()).isEqualTo(2);
        assertThat(orderDetails.getOrderItems().get(0).getProduct().getId()).isEqualTo(product1.getId());
        assertThat(orderDetails.getOrderItems().get(1).getProduct().getId()).isEqualTo(product2.getId());
        assertThat(orderDetails.getOrderItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(orderDetails.getOrderItems().get(1).getQuantity()).isEqualTo(1);
        // check remaining stock levels
        Inventory inventory1 = inventoryRepository.findByStoreAndProduct(store1, product1).orElseThrow();
        Inventory inventory2 = inventoryRepository.findByStoreAndProduct(store1, product2).orElseThrow();
        assertThat(inventory1.getStockLevel()).isEqualTo(0);
        assertThat(inventory2.getStockLevel()).isEqualTo(0);
    }

    @Test
    void saveOrder_whenStockIsInsufficient_returnException() {
        // Arrange
        int wrongQuantity = 3;
        PlaceOrderRequestDTO placeOrderRequest = new PlaceOrderRequestDTO(
                store1.getId(),
                customer1.getName(),
                customer1.getEmail(),
                customer1.getPhone(),
                LocalDateTime.now().toString(),
                List.of(
                        new PurchaseProductDTO(product1.getId(), product1.getName(), product1.getPrice(), wrongQuantity,
                                750.00)));
        // Action, Assert
        assertThatThrownBy(() -> orderService.saveOrder(placeOrderRequest))
                .isInstanceOf(InventoryStockInsufficientException.class)
                .hasMessageContaining("insufficient");

        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        List<OrderItem> orderItems = orderItemRepository.findAll();
        assertThat(orderDetailsList.size()).isEqualTo(0);
        assertThat(orderItems.size()).isEqualTo(0);
        // check remaining stock levels
        Inventory inventory1 = inventoryRepository.findByStoreAndProduct(store1, product1).orElseThrow();
        Inventory inventory2 = inventoryRepository.findByStoreAndProduct(store1, product2).orElseThrow();
        assertThat(inventory1.getStockLevel()).isEqualTo(2);
        assertThat(inventory2.getStockLevel()).isEqualTo(1);
    }

    @Test
    void saveOrder_whenStoreIsNotExist_returnException() {
        // Arrange
        long wrongStoreId = 99L;
        PlaceOrderRequestDTO placeOrderRequest = new PlaceOrderRequestDTO(
                wrongStoreId,
                customer1.getName(),
                customer1.getEmail(),
                customer1.getPhone(),
                LocalDateTime.now().toString(),
                List.of(
                        new PurchaseProductDTO(product1.getId(), product1.getName(), product1.getPrice(), 2, 500.00)));
        // Action, Assert
        assertThatThrownBy(() -> orderService.saveOrder(placeOrderRequest))
                .isInstanceOf(StoreNotFoundException.class)
                .hasMessageContaining("not found id=99");
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        List<OrderItem> orderItems = orderItemRepository.findAll();
        assertThat(orderDetailsList.size()).isEqualTo(0);
        assertThat(orderItems.size()).isEqualTo(0);
        // check remaining stock levels
        Inventory inventory1 = inventoryRepository.findByStoreAndProduct(store1, product1).orElseThrow();
        Inventory inventory2 = inventoryRepository.findByStoreAndProduct(store1, product2).orElseThrow();
        assertThat(inventory1.getStockLevel()).isEqualTo(2);
        assertThat(inventory2.getStockLevel()).isEqualTo(1);
    }

    @Test
    void saveOrder_whenProductIsNotExist_returnException() {
        // Arrange
        long wrongProductId = 99L;
        PlaceOrderRequestDTO placeOrderRequest = new PlaceOrderRequestDTO(
                store1.getId(),
                customer1.getName(),
                customer1.getEmail(),
                customer1.getPhone(),
                LocalDateTime.now().toString(),
                List.of(
                        new PurchaseProductDTO(wrongProductId, product1.getName(), product1.getPrice(), 2, 500.00)));
        // Action, Assert
        assertThatThrownBy(() -> orderService.saveOrder(placeOrderRequest))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("not found id=99");

        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        List<OrderItem> orderItems = orderItemRepository.findAll();
        assertThat(orderDetailsList.size()).isEqualTo(0);
        assertThat(orderItems.size()).isEqualTo(0);
        // check remaining stock levels
        Inventory inventory1 = inventoryRepository.findByStoreAndProduct(store1, product1).orElseThrow();
        Inventory inventory2 = inventoryRepository.findByStoreAndProduct(store1, product2).orElseThrow();
        assertThat(inventory1.getStockLevel()).isEqualTo(2);
        assertThat(inventory2.getStockLevel()).isEqualTo(1);
    }

}

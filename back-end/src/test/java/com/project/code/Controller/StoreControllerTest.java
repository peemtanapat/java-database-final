package com.project.code.Controller;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.code.Model.ApiResponse;
import com.project.code.Model.Customer;
import com.project.code.Model.Inventory;
import com.project.code.Model.OrderDetails;
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

@SpringBootTest
@AutoConfigureMockMvc
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void placeOrder_whenStockIsSufficient() throws Exception {
        String requestBody = objectMapper.writeValueAsString(new PlaceOrderRequestDTO(
                store1.getId(),
                customer1.getName(),
                customer1.getEmail(),
                customer1.getPhone(),
                LocalDateTime.now().toString(),
                List.of(
                        new PurchaseProductDTO(product1.getId(), product1.getName(), product1.getPrice(), 2, 500.00),
                        new PurchaseProductDTO(product2.getId(), product2.getName(), product2.getPrice(), 1, 300.00))));

        MvcResult mvcResult = mockMvc.perform(post("/store/placeOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ApiResponse<OrderDetails> response = objectMapper.readValue(
                contentAsString,
                new TypeReference<ApiResponse<OrderDetails>>() {
                });

        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getMessage()).isEqualTo("Place order successfully");
    }
}

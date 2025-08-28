package com.project.code.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

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
import com.project.code.exception.CustomerNotFoundException;
import com.project.code.exception.InventoryNotFoundException;
import com.project.code.exception.InventorySaveException;
import com.project.code.exception.InventoryStockInsufficientException;
import com.project.code.exception.OrderDetailSaveException;
import com.project.code.exception.OrderItemSaveException;
import com.project.code.exception.ProductNotFoundException;
import com.project.code.exception.StoreNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(rollbackOn = {
            CustomerNotFoundException.class,
            StoreNotFoundException.class,
            ProductNotFoundException.class,
            InventoryNotFoundException.class,
            InventoryStockInsufficientException.class,
            InventorySaveException.class
    })
    public OrderDetails saveOrder(PlaceOrderRequestDTO request) {
        // get/create customer
        Customer customer = customerRepository.findByEmail(request.getCustomerEmail())
                .orElseGet(() -> customerRepository.save(
                        new Customer(
                                request.getCustomerName(),
                                request.getCustomerEmail(),
                                request.getCustomerPhone())));

        // check store
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreNotFoundException("Store not found id=" + request.getStoreId()));

        List<OrderItem> orderItems = new ArrayList<>();
        OrderDetails orderDetails = orderDetailsRepository.save(new OrderDetails(customer, store));

        for (PurchaseProductDTO requestProduct : request.getPurchaseProduct()) {
            Product product = productRepository.findById(requestProduct.getId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found id=" + requestProduct.getId()));
            // get inventory by storeId, productId
            Inventory inventory = inventoryRepository.findByStoreAndProduct(store, product)
                    .orElseThrow(() -> new InventoryNotFoundException("Inventory of StoreId=%s ProductId=%s not found"
                            .formatted(store.getId(), requestProduct.getId())));

            if (inventory.getStockLevel() < requestProduct.getQuantity()) {
                throw new InventoryStockInsufficientException(
                        "Inventory stock insufficient of StoreId=%s ProductId=%s"
                                .formatted(store.getId(), requestProduct.getId()));
            }

            // deduct stockLevel of that inventory product
            inventory.setStockLevel(inventory.getStockLevel() - requestProduct.getQuantity());

            try {
                inventoryRepository.save(inventory);
            } catch (Exception e) {
                throw new InventorySaveException("Saving inventory got exception:" + e.getMessage());
            }

            try {
                OrderItem savedOrderItem = orderItemRepository
                        .save(new OrderItem(orderDetails, product, requestProduct.getQuantity(),
                                product.getPrice()));
                orderItems.add(savedOrderItem);
            } catch (Exception e) {
                throw new OrderItemSaveException("Saving OrderItem got exception:" + e.getMessage());
            }

        }

        try {
            orderDetails.setTotalPrice(
                    orderItems.stream()
                            .mapToDouble(order -> order.getPrice() * order.getQuantity()).sum());
            return orderDetailsRepository.save(orderDetails);
        } catch (Exception e) {
            throw new OrderDetailSaveException("Saving OrderDetail got exception:" + e.getMessage());
        }
    }

    // 1. **saveOrder Method**:
    // - Processes a customer's order, including saving the order details and
    // associated items.
    // - Parameters: `PlaceOrderRequestDTO placeOrderRequest` (Request data for
    // placing an order)
    // - Return Type: `void` (This method doesn't return anything, it just processes
    // the order)

    // 2. **Retrieve or Create the Customer**:
    // - Check if the customer exists by their email using `findByEmail`.
    // - If the customer exists, use the existing customer; otherwise, create and
    // save a new customer using `customerRepository.save()`.

    // 3. **Retrieve the Store**:
    // - Fetch the store by ID from `storeRepository`.
    // - If the store doesn't exist, throw an exception. Use
    // `storeRepository.findById()`.

    // 4. **Create OrderDetails**:
    // - Create a new `OrderDetails` object and set customer, store, total price,
    // and the current timestamp.
    // - Set the order date using `java.time.LocalDateTime.now()` and save the order
    // with `orderDetailsRepository.save()`.

    // 5. **Create and Save OrderItems**:
    // - For each product purchased, find the corresponding inventory, update stock
    // levels, and save the changes using `inventoryRepository.save()`.
    // - Create and save `OrderItem` for each product and associate it with the
    // `OrderDetails` using `orderItemRepository.save()`.

}

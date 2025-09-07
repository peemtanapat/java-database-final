package com.project.code.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.ApiResponse;
import com.project.code.Model.OrderDetails;
import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.StoreDto;
import com.project.code.Service.OrderService;
import com.project.code.Service.StoreService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {

    private final OrderService orderService;
    private final StoreService storeService;

    @PostMapping("/placeOrder")
    public ApiResponse<OrderDetails> placeOrder(@RequestBody PlaceOrderRequestDTO request) {

        OrderDetails orderDetails = orderService.saveOrder(request);

        return new ApiResponse<OrderDetails>("success", "Place order successfully", orderDetails);
    }

    @PostMapping("/")
    public ApiResponse<Boolean> addStore(@RequestBody StoreDto storeDto) {
        boolean isAdded = storeService.addStore(storeDto);

        return new ApiResponse<Boolean>("success", "add a new store successfully", isAdded);
    }

    @GetMapping("/{storeId}")
    public ApiResponse<Boolean> validateStore(@RequestParam long storeId) {
        boolean isExist = storeService.isExistStore(storeId);

        return new ApiResponse<Boolean>("success", "validate store successfully", isExist);
    }

}

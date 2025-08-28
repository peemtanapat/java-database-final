package com.project.code.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.ApiResponse;
import com.project.code.Model.OrderDetails;
import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/placeOrder")
    public ApiResponse<OrderDetails> placeOrder(@RequestBody PlaceOrderRequestDTO request) {

        OrderDetails orderDetails = orderService.saveOrder(request);

        return new ApiResponse<OrderDetails>("success", "Place order successfully", orderDetails);
    }

}

package com.project.code.Controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.project.code.Model.ApiResponse;
import com.project.code.exception.ProductNotFoundException;
import com.project.code.exception.StoreNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StoreNotFoundException.class)
    public ApiResponse<?> handleStoreNotFound(StoreNotFoundException ex) {
        return new ApiResponse<>("error", ex.getMessage(), null);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ApiResponse<?> handleProductNotFound(ProductNotFoundException ex) {
        return new ApiResponse<>("error", ex.getMessage(), null);
    }

}

package com.project.code.exception;

public class InventoryStockInsufficientException extends RuntimeException {

    public InventoryStockInsufficientException(String message) {
        super(message);
    }
}

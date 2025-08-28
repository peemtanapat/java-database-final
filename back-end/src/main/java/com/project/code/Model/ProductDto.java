package com.project.code.Model;

public record ProductDto(
        Long id,
        String name,
        String category,
        Double price,
        String sku) {

}

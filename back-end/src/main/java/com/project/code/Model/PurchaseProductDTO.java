package com.project.code.Model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PurchaseProductDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
    private Double total;

    public PurchaseProductDTO(
            Long productId,
            String name,
            Double price,
            Integer quantity,
            Double total) {
        this.id = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}

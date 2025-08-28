package com.project.code.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference("order-orderItems")
    private OrderDetails order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference("orderItem-product")
    private Product product;

    @NotNull(message = "Quantity cannot be null")
    private int quantity;

    @NotNull(message = "Price cannot be null")
    private double price;

    public OrderItem(
            OrderDetails orderDetail,
            Product product,
            int quantity,
            double price) {
        this.order = orderDetail;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

}

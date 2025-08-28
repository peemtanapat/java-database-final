package com.project.code.Model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference("order-customer") // to handle bidirectional relationships and JSON serialization.
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "store_id")
    @JsonBackReference // to handle bidirectional relationships and JSON serialization.
    private Store store;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    @JsonManagedReference("order-orderItems")
    private List<OrderItem> orderItems;

    private double totalPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;

    public OrderDetails(Customer customer, Store store) {
        this.customer = customer;
        this.store = store;
        this.orderDate = LocalDateTime.now();
    }

}

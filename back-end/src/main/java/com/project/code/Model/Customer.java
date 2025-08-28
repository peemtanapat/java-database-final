package com.project.code.Model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Email cannot be null")
    @Column(unique = true)
    private String email;

    @NotNull(message = "Phone cannot be null")
    private String phone;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER) // A customer can have multiple orders.
    @JsonManagedReference("order-customer") // @JsonManagedReference to ensure proper JSON serialization of related
                                            // orders.
    private List<OrderDetails> orderDetails;

    public Customer(@NotNull(message = "Name cannot be null") String name,
            @NotNull(message = "Email cannot be null") String email,
            @NotNull(message = "Phone cannot be null") String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

}

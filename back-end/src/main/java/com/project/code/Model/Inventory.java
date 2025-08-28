package com.project.code.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
public class Inventory implements AutoCloseable {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   @ManyToOne
   @JoinColumn(name = "store_id")
   @JsonBackReference("inventory-store")
   private Store store;

   @ManyToOne
   @JoinColumn(name = "product_id")
   @JsonBackReference("inventory-product")
   private Product product;
   // 2. Add 'product' field:
   // - Type: private Product
   // - This field will represent the product associated with the inventory entry.
   // - Use @ManyToOne to establish a many-to-one relationship with the Product
   // entity.

   // 3. Add 'store' field:
   // - Type: private Store
   // - This field will represent the store where the inventory is located.
   // - Use @ManyToOne to establish a many-to-one relationship with the Store
   // entity.

   private int stockLevel;

   public Inventory(Store store, Product product, int stockLevel) {
      // TODO: validate stockLevel never be negative
      this.store = store;
      this.product = product;
      this.stockLevel = stockLevel;
   }

   @Override
   public void close() throws Exception {
      log.debug("Inventory object is closed");
   }

}

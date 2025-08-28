package com.project.code.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
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

   @Min(value = 0, message = "Stock level cannot be negative")
   private int stockLevel;

   public Inventory(Store store, Product product, int stockLevel) {
      if (stockLevel < 0) {
         throw new IllegalArgumentException("Stock level cannot be negative");
      }
      this.store = store;
      this.product = product;
      this.stockLevel = stockLevel;
   }

   public void setStockLevel(int stockLevel) {
      if (stockLevel < 0) {
         throw new IllegalArgumentException("Stock level cannot be negative");
      }
      this.stockLevel = stockLevel;
   }

   @Override
   public void close() throws Exception {
      log.debug("Inventory object is closed");
   }

}

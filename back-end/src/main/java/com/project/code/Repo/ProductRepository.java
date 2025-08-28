package com.project.code.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.code.Model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);

    public List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    Product findBySku(String sku);

    Product findByName(String name);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:pname% AND EXISTS (SELECT i FROM Inventory i WHERE i.product = p AND i.store.id = :storeId)")
    List<Product> findByNameLike(Long storeId, String pname);

    @Query("SELECT p FROM Product p WHERE p.category = :category")
    List<Product> findProductByCategory(String category);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:subName% AND p.category = :category")
    List<Product> findProductBySubNameAndCategory(String subName, String category);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:subName%")
    List<Product> findProductBySubName(String subName);

}

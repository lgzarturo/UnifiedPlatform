package com.lgzarturo.onlinestoreapi.products;

import com.lgzarturo.onlinestoreapi.categories.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("FROM Product p WHERE p.deleted = com.lgzarturo.onlinestoreapi.products.ProductDeleted.CREATED AND p.status = :status")
    List<Product> findAll(@Param("status") ProductStatus status);
    List<Product> findByCategoryAndDeleted(Category category, ProductDeleted deleted);
}

package com.lgzarturo.onlinestoreapi.products;

import com.lgzarturo.onlinestoreapi.categories.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    void whenGetAllProducts_thenReturnAllProducts() {
        var list = productRepository.findAll(ProductStatus.NEW);
        assertEquals(2, list.size());
    }

    @Test
    void whenValidGetId_thenReturnProduct() {
        Optional<Product> product = productRepository.findById(1L);
        assertTrue(product.isPresent());
        assertEquals("Book 1", product.get().getName());
    }

    @Test
    void whenInvalidGetId_thenReturnNull() {
        Optional<Product> product = productRepository.findById(105L);
        assertThrows(NoSuchElementException.class, product::orElseThrow);
        assertTrue(product.isEmpty());
    }

    @Test
    void whenValidSaveProduct_thenProductShouldBeReturned() {
        var product = Product.builder()
                .name("Product 3")
                .sku("SKU-1003")
                .stock(100)
                .price(BigDecimal.valueOf(10.0))
                .status(ProductStatus.NEW)
                .deleted(ProductDeleted.CREATED)
                .category(Category.builder().id(1L).build())
                .build();
        var savedProduct = productRepository.save(product);
        var productsFound = productRepository.findByCategoryAndDeleted(product.getCategory(), ProductDeleted.CREATED);
        assertNotNull(savedProduct);
        assertEquals(product.getName(), savedProduct.getName());
        assertEquals(3, productsFound.size());
    }
}
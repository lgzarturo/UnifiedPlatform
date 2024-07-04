package com.lgzarturo.onlinestoreapi.products;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgzarturo.common.dto.common.Currency;

import com.lgzarturo.common.dto.products.ContentType;
import com.lgzarturo.common.dto.products.DescriptionContent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getAllProducts() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setCurrency(Currency.USD);
        product.setDescription("Test description");
        product.setName("Test name");
        product.setPrice(BigDecimal.TEN);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setCurrency(Currency.USD);
        product2.setDescription("Test description2");
        product2.setName("Test name2");
        product2.setPrice(BigDecimal.TEN);

        Product product3 = new Product();
        product3.setId(3L);
        product3.setCurrency(Currency.USD);
        product3.setDescription("Test description3");
        product3.setName("Test name3");
        product3.setPrice(BigDecimal.TEN);

        List<Product> products = List.of(product, product2, product3);

        given(productService.getProducts(any(Pageable.class))).willReturn(products);

        mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllProductsWhenIsEmptyProductsTable() throws Exception {
        List<Product> products = new ArrayList<>();

        given(productService.getProducts(any(Pageable.class))).willReturn(products);

        mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createProduct() throws Exception {
        Product product = new Product();
        product.setName("Product 1");
        product.setDescription("Product 1");
        product.setCurrency(Currency.MXN);
        product.setPrice(BigDecimal.valueOf(100));

        given(productService.saveProduct(any(Product.class))).willReturn(product);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product))
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.description").value(product.getDescription()))
                .andExpect(jsonPath("$.currency").value("MXN"))
                .andExpect(jsonPath("$.price").value(product.getPrice()));
    }

    @Test
    public void getProductById() throws Exception {
        Long id = 1L;
        Product product = new Product();
        product.setId(id);
        product.setName("Product 1");
        product.setDescription("Product 1");
        product.setCurrency(Currency.MXN);
        product.setPrice(BigDecimal.valueOf(100));
        given(productService.getProductById(id)).willReturn(Optional.of(product));

        mockMvc.perform(get("/products/{id}", id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.description").value(product.getDescription()))
                .andExpect(jsonPath("$.currency").value("MXN"))
                .andExpect(jsonPath("$.price").value(product.getPrice()));
    }

    @Test
    public void getProductByIdWhenProductNotFound() throws Exception {
        Long id = 1L;
        given(productService.getProductById(id)).willReturn(Optional.empty());

        mockMvc.perform(get("/products/{id}", id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateProduct() throws Exception {
        Long id = 1L;
        Product product = new Product();
        product.setId(id);
        product.setName("Product 1");
        product.setDescription("Product 1");
        product.setCurrency(Currency.MXN);
        product.setPrice(BigDecimal.valueOf(100));

        Product updatedProduct = new Product();
        updatedProduct.setName("Product (updated)");

        given(productService.getProductById(id)).willReturn(Optional.of(product));
        given(productService.saveProduct(product)).willReturn(product);

        mockMvc.perform(put("/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(updatedProduct.getName()))
                .andExpect(jsonPath("$.description").value(product.getDescription()))
                .andExpect(jsonPath("$.currency").value("MXN"))
                .andExpect(jsonPath("$.price").value(product.getPrice()));
    }

    @Test
    public void updateRichDescription() throws Exception {
        Long id = 1L;
        Product product = new Product();
        product.setId(id);
        product.setName("Product 1");
        product.setDescription("Product 1");

        DescriptionContent descriptionContent = new DescriptionContent();
        descriptionContent.setContent("## Markdown **format**");
        descriptionContent.setContentType(ContentType.MARKDOWN);

        given(productService.getProductById(id)).willReturn(Optional.of(product));
        given(productService.isValidDescription(descriptionContent)).willReturn(true);
        product.setDescription(descriptionContent.getContent());
        given(productService.updateDescription(product, descriptionContent)).willReturn(product);

        mockMvc.perform(patch("/products/{id}/rich-description", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(descriptionContent))
        ).andExpect(status().isAccepted())
                .andExpect(jsonPath("$.description").value(descriptionContent.getContent()));
    }

    @Test
    public void deleteProductById() throws Exception {
        Long id = 1L;
        Product product = new Product();
        product.setId(id);
        product.setName("Product 1");
        product.setDescription("Product 1");
        product.setCurrency(Currency.MXN);
        product.setPrice(BigDecimal.valueOf(100));

        given(productService.getProductById(id)).willReturn(Optional.of(product));

        mockMvc.perform(delete("/products/{id}", id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}
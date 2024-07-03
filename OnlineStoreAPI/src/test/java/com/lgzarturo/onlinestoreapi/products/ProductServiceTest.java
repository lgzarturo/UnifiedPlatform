package com.lgzarturo.onlinestoreapi.products;

import com.lgzarturo.common.dto.common.Currency;
import com.lgzarturo.common.dto.products.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getProductsTest() {
        Product product1 = new Product();
        product1.setName("Product 1");
        Product product2 = new Product();
        product2.setName("Product 2");
        List<Product> productList = Arrays.asList(product1, product2);
        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        List<Product> result = productService.getProducts(Pageable.unpaged());

        assertEquals(2, result.size());
        verify(productRepository).findAll(any(Pageable.class));
    }

    @Test
    void saveProductTest() {
        Product product = new Product();
        product.setName("Product 1");
        product.setSku("SKU-2001");
        product.setDescription("Description");
        product.setStock(10);
        product.setCurrency(Currency.USD);
        product.setPrice(BigDecimal.valueOf(100));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.saveProduct(product);

        assertEquals(product, result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updatePriceTest() {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(100));

        PriceChange increaseAmount = new PriceChange(Currency.MXN, BigDecimal.valueOf(10), ValueType.AMOUNT, PriceChangeType.INCREASE);
        PriceChange decreasePercentage = new PriceChange(Currency.MXN, BigDecimal.valueOf(0.1), ValueType.PERCENTAGE, PriceChangeType.DECREASE); // 10%

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product increasedPriceProduct = productService.updatePrice(product, increaseAmount);
        assertEquals(0, BigDecimal.valueOf(110).compareTo(increasedPriceProduct.getPrice()));

        Product decreasedPriceProduct = productService.updatePrice(product, decreasePercentage);
        assertEquals(0, BigDecimal.valueOf(90).compareTo(decreasedPriceProduct.getPrice()));
    }

    @Test
    void getProductByIdTest() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(productId);

        assertTrue(result.isPresent());
        assertEquals(productId, result.get().getId());
    }

    @Test
    void deleteProductTest() {
        Long productId = 1L;
        productService.deleteProduct(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    void updateDescriptionTest() {
        Product product = new Product();
        product.setDescription("Old Description");

        DescriptionContent newDescription = new DescriptionContent("New Description", ContentType.PLAIN_TEXT);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product updatedProduct = productService.updateDescription(product, newDescription);

        assertNotNull(updatedProduct.getDescription());
        assertTrue(updatedProduct.getDescription().contains("New Description"));
    }


    @Test
    void isValidDescriptionWithValidHTML() {
        DescriptionContent validHtmlContent = new DescriptionContent("<p>Hello World</p>", ContentType.HTML);
        assertTrue(productService.isValidDescription(validHtmlContent));
    }

    @Test
    void isValidDescriptionWithInvalidHTML() {
        DescriptionContent invalidHtmlContent = new DescriptionContent("<script>alert('Hi')</script>", ContentType.HTML);
        assertFalse(productService.isValidDescription(invalidHtmlContent));
    }

    @Test
    void isValidDescriptionWithValidMarkdown() {
        DescriptionContent validMarkdownContent = new DescriptionContent("# Hello World", ContentType.MARKDOWN);
        assertTrue(productService.isValidDescription(validMarkdownContent));
    }

    @Test
    void isValidDescriptionWithInvalidMarkdown() {
        DescriptionContent invalidMarkdownContent = new DescriptionContent("", ContentType.MARKDOWN);
        assertFalse(productService.isValidDescription(invalidMarkdownContent));
    }

    @Test
    void isValidDescriptionWithValidPlainText() {
        DescriptionContent validPlainTextContent = new DescriptionContent("<p>Plain text</p>", ContentType.PLAIN_TEXT);
        assertTrue(productService.isValidDescription(validPlainTextContent));
    }

    @Test
    void isValidDescriptionWithInvalidPlainText() {
        DescriptionContent invalidPlainTextContent = new DescriptionContent("", ContentType.PLAIN_TEXT);
        assertFalse(productService.isValidDescription(invalidPlainTextContent));
    }

    @Test
    void validationMarkdownContentWithValidContent() {
        assertTrue(productService.validationMarkdownContent("# Hello World"));
    }

    @Test
    void validationMarkdownContentWithInvalidContent() {
        assertFalse(productService.validationMarkdownContent(""));
    }

    @Test
    void validationHTMLContentWithValidContent() {
        assertTrue(productService.validationHTMLContent("<p>Hello World</p>"));
    }

    @Test
    void validationHTMLContentWithInvalidContent() {
        assertFalse(productService.validationHTMLContent("<p></p>"));
    }

    @Test
    void cleanHTMLContentRemovesUnsafeTags() {
        String unsafeHtml = "<script>alert('Hi')</script><p>Safe Content</p>";
        String cleanedHtml = productService.cleanHTMLContent(unsafeHtml);
        assertFalse(cleanedHtml.contains("<script>"));
    }
}
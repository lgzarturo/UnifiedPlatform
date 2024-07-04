package com.lgzarturo.onlinestoreapi.products;

import com.lgzarturo.common.dto.products.DescriptionContent;
import com.lgzarturo.common.dto.products.PriceChange;
import com.lgzarturo.common.dto.products.PriceChangeType;
import com.lgzarturo.common.dto.products.ValueType;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import org.jsoup.safety.Safelist;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable).getContent();
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updatePrice(Product product, PriceChange priceChange) {
        var price = product.getPrice();
        var value = priceChange.getAmount();
        var valueType = priceChange.getValueType();
        var type = priceChange.getType();
        if (valueType == ValueType.AMOUNT) {
            if (type == PriceChangeType.INCREASE) {
                price = price.add(value);
            } else {
                price = price.subtract(value);
            }
        } else {
            if (type == PriceChangeType.INCREASE) {
                if (value.compareTo(BigDecimal.ONE) > 0) {
                    value = value.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                }
                price = price.multiply(value.add(BigDecimal.ONE));
            } else {
                if (value.compareTo(BigDecimal.ONE) > 0) {
                    value = value.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                }
                price = price.subtract(price.multiply(value));
            }
        }
        if (price.compareTo(new BigDecimal(0)) > 0) {
            product.setPrice(price);
        } else {
            product.setPrice(BigDecimal.ZERO);
        }
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Product updateDescription(Product productValue, DescriptionContent description) {
        var descriptionValue = productValue.getDescription();
        var content = description.getContent();
        var contentType = description.getContentType();
        descriptionValue = switch (contentType) {
            case HTML -> cleanHTMLContent(content);
            case MARKDOWN -> content;
            case PLAIN_TEXT -> "<p>" + content + "</p>";
        };
        productValue.setDescription(descriptionValue);
        return productRepository.save(productValue);
    }

    public boolean isValidDescription(DescriptionContent description) {
        var content = description.getContent();
        var contentType = description.getContentType();
        if (content.isBlank()) {
            return false;
        }
        return switch (contentType) {
            case HTML -> validationHTMLContent(content);
            case MARKDOWN -> validationMarkdownContent(content);
            case PLAIN_TEXT -> content.startsWith("<p>") && content.endsWith("</p>");
        };
    }

    boolean validationMarkdownContent(String content) {
        Parser parser = Parser.builder().build();
        Document document = parser.parse(content);
        return document.hasChildren();
    }

    boolean validationHTMLContent(String content) {
        try {
            org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(content);
            return document.hasText();
        } catch (Exception e) {
            return false;
        }
    }

    String cleanHTMLContent(String content) {
        return org.jsoup.Jsoup.clean(content, Safelist.simpleText());
    }
}

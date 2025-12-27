package com.lgzarturo.onlinestoreapi.products;

import com.lgzarturo.common.dto.products.DescriptionContent;
import com.lgzarturo.common.dto.products.PriceChange;
import com.lgzarturo.common.dto.products.PriceRequest;
import com.lgzarturo.common.dto.products.StockRequest;
import com.lgzarturo.common.libs.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.lgzarturo.common.libs.Constants.*;

@RestController
@RequestMapping("/v1/products")
public class ProductController {
    private final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<?>> getProducts(Pageable pageable) {
        try {
            var products = productService.getProducts(pageable);
            if (products.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error al obtener los productos", e);
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            var product = productService.getProductById(id);
            return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener el producto", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> saveProduct(@RequestBody Product product) {
        try {
            var uri = URI.create("products/" + product.getId());
            return ResponseEntity.created(uri).body(productService.saveProduct(product));
        } catch (Exception e) {
            log.error("Error al guardar el producto", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            var productToUpdate = productService.getProductById(id);
            if (productToUpdate.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            var productToUpdateValue = productToUpdate.get();
            if (product.getName() != null && !Objects.equals(product.getName(), productToUpdateValue.getName())) {
                productToUpdateValue.setName(product.getName());
            }
            if (product.getSku() != null && !Objects.equals(product.getSku(), productToUpdateValue.getSku())) {
                productToUpdateValue.setSku(product.getSku());
            }
            if (product.getDescription() != null && !Objects.equals(product.getDescription(), productToUpdateValue.getDescription())) {
                productToUpdateValue.setDescription(product.getDescription());
            }
            if (product.getPrice() != null && !Objects.equals(product.getPrice(), productToUpdateValue.getPrice())) {
                productToUpdateValue.setPrice(product.getPrice());
            }
            if (product.getStock() != null && !Objects.equals(product.getStock(), productToUpdateValue.getStock())) {
                productToUpdateValue.setStock(product.getStock());
            }
            if (product.getImageUrl() != null && !Objects.equals(product.getImageUrl(), productToUpdateValue.getImageUrl())) {
                productToUpdateValue.setImageUrl(product.getImageUrl());
            }
            if (!Objects.equals(product.isActive(), productToUpdateValue.isActive())) {
                productToUpdateValue.setActive(product.isActive());
            }
            return ResponseEntity.ok(productService.saveProduct(productToUpdateValue));
        } catch (Exception e) {
            log.error("Error al actualizar el producto", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/rich-description")
    public ResponseEntity<?> updateRichDescription(@PathVariable Long id, @RequestBody DescriptionContent description) {
        try {
            var product = productService.getProductById(id);
            if (product.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            var productValue = product.get();
            if (productService.isValidDescription(description)) {
                return ResponseEntity.accepted().body(productService.updateDescription(productValue, description));
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error al actualizar la descripción del producto", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/increase")
    public ResponseEntity<?> addStock(@PathVariable Long id, @RequestBody StockRequest stock) {
        try {
            var product = productService.getProductById(id);
            if (product.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            var productValue = product.get();
            productValue.setStock(productValue.getStock() + stock.getQuantity());
            return ResponseEntity.ok(productService.saveProduct(productValue));
        } catch (Exception e) {
            log.error("Error al aumentar el stock del producto", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/decrease")
    public ResponseEntity<?> removeStock(@PathVariable Long id, @RequestBody StockRequest stock) {
        try {
            var product = productService.getProductById(id);
            if (product.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            var productValue = product.get();
            var stockUpdate = productValue.getStock() - stock.getQuantity();
            productValue.setStock(Math.max(stockUpdate, 0));
            return ResponseEntity.ok(productService.saveProduct(productValue));
        } catch (Exception e) {
            log.error("Error al disminuir el stock del producto", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activateProduct(@PathVariable Long id) {
        try {
            var product = productService.getProductById(id);
            if (product.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            var productValue = product.get();
            productValue.setActive(true);
            return ResponseEntity.ok(productService.saveProduct(productValue));
        } catch (Exception e) {
            log.error("Error al activar el producto", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateProduct(@PathVariable Long id) {
        try {
            var product = productService.getProductById(id);
            if (product.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            var productValue = product.get();
            productValue.setActive(false);
            return ResponseEntity.ok(productService.saveProduct(productValue));
        } catch (Exception e) {
            log.error("Error al desactivar el producto", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/set-price")
    public ResponseEntity<?> setPrice(@PathVariable Long id, @RequestBody PriceRequest price) {
        try {
            var product = productService.getProductById(id);
            if (product.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            var productValue = product.get();
            productValue.setPrice(price.getAmount());
            productValue.setCurrency(price.getCurrency());
            return ResponseEntity.ok(productService.saveProduct(productValue));
        } catch (Exception e) {
            log.error("Error al establecer el precio del producto", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/modify-price")
    public ResponseEntity<?> increasePrice(@PathVariable Long id, @RequestBody PriceChange priceChange) {
        try {
            var product = productService.getProductById(id);
            if (product.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            var productValue = product.get();
            if (productValue.getCurrency() != null && !productValue.getCurrency().equals(priceChange.getCurrency())) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(productService.updatePrice(productValue, priceChange));
        } catch (Exception e) {
            log.error("Error al modificar el precio del producto", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/upload-image")
    public ResponseEntity<?> uploadImage(@PathVariable Long id, @RequestBody MultipartFile image) {
        try {
            var product = productService.getProductById(id);
            if (product.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            if (image.getSize() > Constants.MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().build();
            }
            if (!List.of(ALLOWED_MIME_TYPES).contains(image.getContentType())) {
                return ResponseEntity.badRequest().build();
            }

            try {
                BufferedImage bufferedImage = javax.imageio.ImageIO.read(image.getInputStream());
                int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();
                if (width > MAX_IMAGE_WIDTH || height > MAX_IMAGE_HEIGHT) {
                    return ResponseEntity.badRequest().build();
                }

                double aspectRatio = (double) width / height;
                if (Math.abs(aspectRatio - 16.0 / 9.0) > 0.01) {
                    return ResponseEntity.badRequest().build();
                }
                if (aspectRatio < 1.77 || aspectRatio > 1.78) {
                    return ResponseEntity.badRequest().build();
                }
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            var productValue = product.get();
            productValue.setImageUrl(image.getOriginalFilename());
            return ResponseEntity.ok(productService.saveProduct(productValue));
        } catch (Exception e) {
            log.error("Error al subir la imagen del producto", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error al eliminar el producto", e);
            return ResponseEntity.badRequest().build();
        }
    }

}

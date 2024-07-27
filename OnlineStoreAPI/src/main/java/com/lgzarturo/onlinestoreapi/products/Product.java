package com.lgzarturo.onlinestoreapi.products;

import com.lgzarturo.common.dto.common.Currency;
import com.lgzarturo.common.libs.CurrencyUtils;
import com.lgzarturo.onlinestoreapi.categories.Category;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.lgzarturo.common.libs.Constants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String sku;
    @Column(nullable = false)
    private String name;
    @Column(length = 8000, columnDefinition = TYPE_TEXT)
    private String description;
    @Enumerated(EnumType.STRING)
    private Currency currency = Currency.valueOf(DEFAULT_CURRENCY);
    @Column(precision = 10, scale = 2)
    @NumberFormat(pattern = CURRENCY_FORMAT, style = NumberFormat.Style.CURRENCY)
    private BigDecimal price;
    @Transient
    private String priceFormatted;
    private Integer stock;
    private String imageUrl;
    private boolean active;
    @Enumerated(EnumType.STRING)
    private ProductStatus status;
    @Enumerated(EnumType.STRING)
    private ProductDeleted deleted;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    void setPrePersist() {
        active = true;
        stock = DEFAULT_STOCK;
        status = ProductStatus.NEW;
        deleted = ProductDeleted.CREATED;
    }

    private String getPriceFormatted() {
        return CurrencyUtils.getFormattedPrice(currency.name(), price);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Product product = (Product) o;
        return getId() != null && Objects.equals(getId(), product.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

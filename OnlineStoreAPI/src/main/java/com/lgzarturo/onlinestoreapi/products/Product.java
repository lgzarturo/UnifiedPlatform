package com.lgzarturo.onlinestoreapi.products;

import com.lgzarturo.common.dto.common.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
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
    @Column(length = 8000, columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    private Currency currency = Currency.MXN;
    @Column(precision = 10, scale = 2)
    @NumberFormat(pattern = "$###,###,###.00", style = NumberFormat.Style.CURRENCY)
    private BigDecimal price;
    @Transient
    private String priceFormatted;
    private Integer stock = 0;
    private String imageUrl;
    private boolean active = true;

    private String getPriceFormatted() {
        var priceValue = price != null ? price : BigDecimal.ZERO;
        return "$" + priceValue + " (" + currency + ")";
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

package ru.yandex.practicum.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.store.enums.ProductCategory;
import ru.yandex.practicum.dto.store.enums.ProductState;
import ru.yandex.practicum.dto.store.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {

    @Id
    @GeneratedValue
    UUID productId;

    @Column(name = "product_name", nullable = false)
    String productName;

    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "image_src")
    String imageSrc;

    @Column(name = "quantity_state")
    @Enumerated(EnumType.STRING)
    QuantityState quantityState;

    @Column(name = "product_state")
    @Enumerated(EnumType.STRING)
    ProductState productState;

    @Column(name = "product_category")
    @Enumerated(EnumType.STRING)
    ProductCategory productCategory;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    BigDecimal price;
}

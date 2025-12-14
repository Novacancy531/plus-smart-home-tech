package ru.yandex.practicum.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "shopping_carts")
public class ShoppingCart {

    @Id
    @GeneratedValue
    UUID shoppingCartId;

    @Column(name = "username", nullable = false)
    String username;

    @Column(name = "active", nullable = false)
    boolean active;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "cart_products",
            joinColumns = @JoinColumn(name = "shopping_cart_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity", nullable = false)
    @Builder.Default
    Map<UUID, Long> products = new HashMap<>();
}

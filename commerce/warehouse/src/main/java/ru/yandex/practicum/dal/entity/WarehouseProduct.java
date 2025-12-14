package ru.yandex.practicum.dal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "warehouse_products")
public class WarehouseProduct {

    @Id
    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID productId;

    @Column(name = "fragile", nullable = false)
    private boolean fragile;

    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "product_id", nullable = false)
    private Dimension dimension;

    @Column(name = "weight", nullable = false)
    private double weight;

    @Column(name = "quantity", nullable = false)
    private long quantity;

    @Version
    @Column(name = "version", nullable = false)
    private long version;
}
package ru.yandex.practicum.entity.warehouse;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewProductInWarehouseRequest {

    UUID productId;
    boolean fragile;
    DimensionDto dimension;
    double weight;
}

package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewProductInWarehouseRequest {

    @NotNull(message = "Заполните идентификатор товара")
    UUID productId;

    boolean fragile;

    @NotNull(message = "Заполните габариты товара")
    DimensionDto dimension;

    @PositiveOrZero(message = "Вес товара не может быть отрицательным")
    double weight;
}

package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseProductDto {

    UUID productId;

    boolean fragile;

    @NotNull(message = "Укажите габариты товара")
    DimensionDto dimensionDto;

    @PositiveOrZero(message = "Вес товара не может быть отрицательным")
    double weight;

    @Positive(message = "Количество товара должно быть больше 0")
    long quantity;
}

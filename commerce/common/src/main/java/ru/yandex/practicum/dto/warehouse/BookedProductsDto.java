package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookedProductsDto {

    @NotNull(message = "Заполните вес доставки.")
    double deliveryWeight;

    @NotNull(message = "Заполните объем доставки.")
    double deliveryVolume;

    boolean fragile;
}

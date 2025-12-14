package ru.yandex.practicum.entity.warehouse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookedProductsDto {

    double deliveryWeight;
    double deliveryVolume;
    boolean fragile;
}

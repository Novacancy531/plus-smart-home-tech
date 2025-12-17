package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddProductToWarehouseRequest {

    @NotNull(message = "Заполните UUID продукта.")
    UUID productId;

    @NotNull(message = "Заполните количество продукта.")
    long quantity;
}

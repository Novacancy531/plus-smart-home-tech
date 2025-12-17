package ru.yandex.practicum.dto.store;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.store.enums.QuantityState;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SetProductQuantityStateRequest {

    @NotNull(message = "Заполните UUID продукта.")
    UUID productId;

    @NotNull(message = "Заполните количество продукта.")
    QuantityState quantityState;
}

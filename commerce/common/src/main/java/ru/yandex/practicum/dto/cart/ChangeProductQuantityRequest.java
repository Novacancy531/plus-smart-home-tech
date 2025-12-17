package ru.yandex.practicum.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeProductQuantityRequest {

    @NotNull(message = "Заполните UUID продукта.")
    UUID productId;

    @NotNull(message = "Заполните количество продукта.")
    long newQuantity;
}

package ru.yandex.practicum.dto.cart;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCartDto {

    @NotNull(message = "Заполните UUID корзины.")
    UUID shoppingCartId;
    @NotEmpty(message = "Добавьте товары.")
    Map<UUID, @Positive Long> products;
}

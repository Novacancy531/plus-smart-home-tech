package ru.yandex.practicum.dto.store;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.store.enums.ProductCategory;
import ru.yandex.practicum.dto.store.enums.ProductState;
import ru.yandex.practicum.dto.store.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {

    UUID productId;

    @NotBlank(message = "Имя продукта не может быть пустым.")
    String productName;

    @NotBlank(message = "Описание продукта не может быть пустым.")
    String description;

    String imageSrc;

    @NotNull(message = "Заполните количество.")
    QuantityState quantityState;

    @NotNull(message = "Заполните состояние.")
    ProductState productState;

    @NotNull(message = "Заполните категорию.")
    ProductCategory productCategory;

    @NotNull(message = "Заполните цену.")
    @PositiveOrZero(message = "Цена не может быть отрицательной.")
    @Digits(integer = 17, fraction = 2, message = "Цена должна быть в формате 12345.67 (не более 2 знаков после запятой)")
    BigDecimal price;
}

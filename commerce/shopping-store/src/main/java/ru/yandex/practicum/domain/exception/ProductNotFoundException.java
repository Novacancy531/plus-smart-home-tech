package ru.yandex.practicum.domain.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductNotFoundException extends RuntimeException {

    final UUID productId;

    public ProductNotFoundException(UUID productId) {
        super("Товар с uuid: " + productId + " не найден");
        this.productId = productId;
    }
}

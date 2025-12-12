package ru.yandex.practicum.domain.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ProductNotFoundException extends RuntimeException {

    private final UUID productId;

    public ProductNotFoundException(UUID productId) {
        super("Товар с uuid: " + productId + " не найден");
        this.productId = productId;
    }
}

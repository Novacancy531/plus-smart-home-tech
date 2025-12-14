package ru.yandex.practicum.domain.exception;

import lombok.Getter;

@Getter
public class CartIsDeactivatedException extends RuntimeException {

    private final String userMessage;

    public CartIsDeactivatedException(String message) {
        super(message);
        this.userMessage = message;
    }
}


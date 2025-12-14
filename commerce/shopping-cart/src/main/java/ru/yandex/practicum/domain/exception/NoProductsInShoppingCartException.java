package ru.yandex.practicum.domain.exception;

import lombok.Getter;

@Getter
public class NoProductsInShoppingCartException extends RuntimeException {

    private final String userMessage;

    public NoProductsInShoppingCartException(String message) {
        super(message);
        this.userMessage = message;
    }
}


package ru.yandex.practicum.domain.exception;

import lombok.Getter;

@Getter
public class NotAuthorizedUserException extends RuntimeException {

    private final String userMessage;

    public NotAuthorizedUserException(String message) {
        super(message);
        this.userMessage = message;
    }
}


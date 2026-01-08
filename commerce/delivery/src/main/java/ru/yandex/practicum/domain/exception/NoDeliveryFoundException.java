package ru.yandex.practicum.domain.exception;

public class NoDeliveryFoundException extends RuntimeException {
    public NoDeliveryFoundException(String message) {
        super(message);
    }
}

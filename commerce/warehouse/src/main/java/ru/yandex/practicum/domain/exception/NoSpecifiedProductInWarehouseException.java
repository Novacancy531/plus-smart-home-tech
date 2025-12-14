package ru.yandex.practicum.domain.exception;

public class NoSpecifiedProductInWarehouseException extends RuntimeException {

    public NoSpecifiedProductInWarehouseException(String message) {
        super(message);
    }
}

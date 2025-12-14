package ru.yandex.practicum.domain.exception;

public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {

    public SpecifiedProductAlreadyInWarehouseException(String message) {
        super(message);
    }
}

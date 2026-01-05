package ru.yandex.practicum.api.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.domain.exception.NoOrderFoundException;
import ru.yandex.practicum.domain.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.dto.ErrorResponse;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(NotEnoughInfoInOrderToCalculateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotAuthorized(NotEnoughInfoInOrderToCalculateException ex) {
        return ErrorResponse.builder()
                .errorCode("UNAUTHORIZED")
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .details(Map.of())
                .build();
    }

    @ExceptionHandler(NoOrderFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoProducts(NoOrderFoundException ex) {
        return ErrorResponse.builder()
                .errorCode("NO_PRODUCTS_IN_SHOPPING_CART")
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .details(Map.of())
                .build();
    }
}

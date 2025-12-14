package ru.yandex.practicum.api.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.domain.exception.CartIsDeactivatedException;
import ru.yandex.practicum.domain.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.domain.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.ErrorResponse;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleNotAuthorized(NotAuthorizedUserException ex) {
        return ErrorResponse.builder()
                .errorCode("UNAUTHORIZED")
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .details(Map.of())
                .build();
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoProducts(NoProductsInShoppingCartException ex) {
        return ErrorResponse.builder()
                .errorCode("NO_PRODUCTS_IN_SHOPPING_CART")
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .details(Map.of())
                .build();
    }

    @ExceptionHandler(CartIsDeactivatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCartIsDeactivated(CartIsDeactivatedException ex) {
        return ErrorResponse.builder()
                .errorCode("CART_IS_DEACTIVATED")
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .details(Map.of())
                .build();
    }
}

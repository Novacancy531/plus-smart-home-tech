package ru.yandex.practicum.api.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.domain.exception.CartIsDeactivatedException;
import ru.yandex.practicum.domain.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.domain.exception.NotAuthorizedUserException;
import ru.yandex.practicum.dto.ErrorResponse;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBodyValidation(MethodArgumentNotValidException ex) {

        Map<String, Object> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> (Object) Objects.requireNonNullElse(fe.getDefaultMessage(),
                                "Некорректное значение"),
                        (a, b) -> a
                ));

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .errorCode("VALIDATION_ERROR")
                        .message("Ошибка валидации запроса")
                        .timestamp(Instant.now())
                        .details(details)
                        .build()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleParamValidation(ConstraintViolationException ex) {

        Map<String, Object> details = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        v -> (Object) Objects.requireNonNullElse(
                                v.getMessage(),
                                "Некорректное значение"
                        ),
                        (a, b) -> a
                ));

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .errorCode("VALIDATION_ERROR")
                        .message("Ошибка валидации параметров")
                        .timestamp(Instant.now())
                        .details(details)
                        .build()
        );
    }
}

package ru.yandex.practicum.api.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.domain.exception.ProductNotFoundException;
import ru.yandex.practicum.dto.ErrorResponse;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        ErrorResponse body = ErrorResponse.builder()
                .errorCode("PRODUCT_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .details(Map.of("productId", ex.getProductId()))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
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

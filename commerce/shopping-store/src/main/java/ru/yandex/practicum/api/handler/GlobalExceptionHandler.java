package ru.yandex.practicum.api.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.domain.exception.ProductNotFoundException;
import ru.yandex.practicum.entity.ErrorResponse;

import java.time.Instant;
import java.util.Map;

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
}


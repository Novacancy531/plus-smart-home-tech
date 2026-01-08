package ru.yandex.practicum.api.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.domain.exception.NoDeliveryFoundException;
import ru.yandex.practicum.dto.ErrorResponse;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoDeliveryFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoProducts(NoDeliveryFoundException ex) {

        log.warn("NoDeliveryFoundException: {}", ex.getMessage());

        return ErrorResponse.builder()
                .errorCode(HttpStatus.NOT_FOUND.toString())
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
                        fe -> (Object) Objects.requireNonNullElse(
                                fe.getDefaultMessage(),
                                "Некорректное значение"
                        ),
                        (a, b) -> a
                ));

        log.warn("Validation error in request body: {}", details);

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

        log.warn("Validation error in request parameters: {}", details);

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

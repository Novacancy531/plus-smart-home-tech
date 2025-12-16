package ru.yandex.practicum.api.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.domain.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.domain.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.domain.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.exception.ErrorResponse;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyInWarehouse(
            SpecifiedProductAlreadyInWarehouseException ex
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .errorCode("SPECIFIED_PRODUCT_ALREADY_IN_WAREHOUSE")
                        .message(ex.getMessage())
                        .timestamp(Instant.now())
                        .details(ex.getDetails())
                        .build()
        );
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ResponseEntity<ErrorResponse> handleNoProduct(
            NoSpecifiedProductInWarehouseException ex
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .errorCode("NO_SPECIFIED_PRODUCT_IN_WAREHOUSE")
                        .message(ex.getMessage())
                        .timestamp(Instant.now())
                        .details(ex.getDetails())
                        .build()
        );
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    public ResponseEntity<ErrorResponse> handleLowQuantity(
            ProductInShoppingCartLowQuantityInWarehouse ex
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .errorCode("PRODUCT_IN_SHOPPING_CART_LOW_QUANTITY_IN_WAREHOUSE")
                        .message(ex.getMessage())
                        .timestamp(Instant.now())
                        .details(ex.getDetails())
                        .build()
        );
    }
}

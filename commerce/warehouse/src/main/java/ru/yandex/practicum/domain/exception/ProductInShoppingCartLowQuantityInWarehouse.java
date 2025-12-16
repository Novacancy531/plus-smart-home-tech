package ru.yandex.practicum.domain.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException {

    private Map<String, Object> details;

    public ProductInShoppingCartLowQuantityInWarehouse(String message,  Map<String, Object> details) {
        super(message);
        this.details = details;
    }
}

package ru.yandex.practicum.domain.exception;

public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException {

    public ProductInShoppingCartLowQuantityInWarehouse(String message) {
        super(message);
    }
}

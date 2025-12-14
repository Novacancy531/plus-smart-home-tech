package ru.yandex.practicum.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.domain.service.ShoppingStoreService;
import ru.yandex.practicum.entity.store.ProductDto;
import ru.yandex.practicum.entity.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.entity.store.enums.ProductCategory;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {

    private final ShoppingStoreService service;

    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable) {
        return service.getProducts(category, pageable);
    }

    @PutMapping
    public ProductDto createProduct(@RequestBody @Valid ProductDto dto) {
        return service.createProduct(dto);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody @Valid ProductDto dto) {
        return service.updateProduct(dto);
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId) {
        return service.getProduct(productId);
    }

    @PostMapping("/removeProductFromStore")
    public boolean removeProduct(@RequestBody UUID productId) {
        return service.removeProduct(productId);
    }

    @PostMapping("/quantityState")
    public boolean setProductQuantityState(@RequestBody SetProductQuantityStateRequest request) {
        return service.setQuantityState(request);
    }
}

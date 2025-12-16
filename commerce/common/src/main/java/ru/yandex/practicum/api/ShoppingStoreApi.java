package ru.yandex.practicum.api;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.entity.store.ProductDto;
import ru.yandex.practicum.entity.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.entity.store.enums.ProductCategory;


import java.util.UUID;

public interface ShoppingStoreApi {

    String PATH = "/api/v1/shopping-store";

    @GetMapping(PATH)
    Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable); // заменить Page из-за data

    @PutMapping(PATH)
    ProductDto createProduct(@RequestBody @Valid ProductDto dto);

    @PostMapping(PATH)
    ProductDto updateProduct(@RequestBody @Valid ProductDto dto);

    @GetMapping(PATH + "/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);

    @PostMapping(PATH + "/removeProductFromStore")
    boolean removeProduct(@RequestBody UUID productId);

    @PostMapping(PATH + "/quantityState")
    boolean setProductQuantityState(@RequestBody SetProductQuantityStateRequest request);
}

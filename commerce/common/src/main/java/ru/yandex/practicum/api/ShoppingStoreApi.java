package ru.yandex.practicum.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.entity.store.ProductDto;
import ru.yandex.practicum.entity.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.entity.store.enums.ProductCategory;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreApi {

    String PATH = "/api/v1/shopping-store";

    @GetMapping(PATH)
    List<ProductDto> getProducts(@RequestParam ProductCategory category, @RequestParam int page,
                                 @RequestParam int size);

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

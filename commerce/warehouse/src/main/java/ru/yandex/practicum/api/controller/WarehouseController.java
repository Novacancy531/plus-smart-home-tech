package ru.yandex.practicum.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.api.WarehouseApi;
import ru.yandex.practicum.domain.service.WarehouseService;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseApi {

    private final WarehouseService service;

    @PutMapping()
    public WarehouseProductDto newProductInWarehouse(@RequestBody @Valid NewProductInWarehouseRequest request) {
        return service.newProductInWarehouse(request);
    }

    @PostMapping("/add")
    public void addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequest request) {
        service.addProductToWarehouse(request);
    }

    @PostMapping("/check")
    public void checkAvailability(@RequestBody @Valid ShoppingCartDto shoppingCartDto) {
        service.checkAvailability(shoppingCartDto);
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return service.getWarehouseAddress();
    }
}


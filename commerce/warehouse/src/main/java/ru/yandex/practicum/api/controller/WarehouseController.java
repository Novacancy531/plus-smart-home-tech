package ru.yandex.practicum.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.api.WarehouseApi;
import ru.yandex.practicum.domain.service.WarehouseService;
import ru.yandex.practicum.entity.cart.ShoppingCartDto;
import ru.yandex.practicum.entity.warehouse.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseApi {

    private final WarehouseService service;

    @PutMapping()
    public WarehouseProductDto newProductInWarehouse(@RequestBody NewProductInWarehouseRequest request) {
        return service.newProductInWarehouse(request);
    }

    @PostMapping("/add")
    public void addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request) {
        service.addProductToWarehouse(request);
    }

    @PostMapping("/check")
    public void checkAvailability(@RequestBody ShoppingCartDto shoppingCartDto) {
        service.checkAvailability(shoppingCartDto);
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return service.getWarehouseAddress();
    }
}


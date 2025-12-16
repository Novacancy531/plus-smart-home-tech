package ru.yandex.practicum.api;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.entity.cart.ShoppingCartDto;
import ru.yandex.practicum.entity.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.entity.warehouse.AddressDto;
import ru.yandex.practicum.entity.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.entity.warehouse.WarehouseProductDto;

public interface WarehouseApi {

    String PATH = "/api/v1/warehouse";

    @PutMapping(PATH)
    WarehouseProductDto newProductInWarehouse(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping(PATH + "/add")
    void addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request);

    @PostMapping(PATH + "/check")
    void checkAvailability(@RequestBody ShoppingCartDto shoppingCartDto);

    @GetMapping(PATH + "/address")
    AddressDto getWarehouseAddress();
}

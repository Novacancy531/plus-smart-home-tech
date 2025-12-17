package ru.yandex.practicum.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.WarehouseProductDto;

public interface WarehouseApi {

    String PATH = "/api/v1/warehouse";

    @PutMapping(PATH)
    WarehouseProductDto newProductInWarehouse(@RequestBody @Valid NewProductInWarehouseRequest request);

    @PostMapping(PATH + "/add")
    void addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequest request);

    @PostMapping(PATH + "/check")
    void checkAvailability(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @GetMapping(PATH + "/address")
    AddressDto getWarehouseAddress();
}

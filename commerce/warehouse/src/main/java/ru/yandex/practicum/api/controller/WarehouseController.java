package ru.yandex.practicum.api.controller;

import jakarta.ws.rs.HttpMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.domain.service.WarehouseService;
import ru.yandex.practicum.entity.cart.ShoppingCartDto;
import ru.yandex.practicum.entity.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.entity.warehouse.AddressDto;
import ru.yandex.practicum.entity.warehouse.BookedProductsDto;
import ru.yandex.practicum.entity.warehouse.NewProductInWarehouseRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {

    private final WarehouseService service;

    @PutMapping()
    public void newProductInWarehouse(@RequestBody NewProductInWarehouseRequest request) {
        service.newProductInWarehouse(request);
    }

    @PostMapping("/add")
    public void addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request) {
        service.addProductToWarehouse(request);
    }

    @PostMapping("/check")
    public BookedProductsDto checkAvailability(@RequestBody ShoppingCartDto shoppingCartDto) {
        return service.checkAvailability(shoppingCartDto);
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return service.getWarehouseAddress();
    }
}


package ru.yandex.practicum.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.domain.service.ShoppingCartService;
import ru.yandex.practicum.entity.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.entity.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController {

    private final ShoppingCartService service;

    @GetMapping
    public ShoppingCartDto get(@RequestParam String username) {
        return service.getShoppingCart(username);
    }

    @PutMapping
    public ShoppingCartDto add(@RequestParam String username,
                               @RequestBody Map<UUID, Long> body) {
        return service.addProducts(username, body);
    }

    @DeleteMapping
    public void deactivate(@RequestParam String username) {
        service.deactivateCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto remove(@RequestParam String username,
                                  @RequestBody List<UUID> productIds) {
        return service.removeProducts(username, productIds);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantity(@RequestParam String username,
                                          @RequestBody ChangeProductQuantityRequest request) {
        return service.changeQuantity(username, request);
    }
}

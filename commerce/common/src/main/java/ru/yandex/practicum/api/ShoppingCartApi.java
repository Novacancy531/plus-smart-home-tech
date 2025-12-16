package ru.yandex.practicum.api;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.entity.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.entity.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartApi {

    String PATH = "/api/v1/shopping-cart";

    @GetMapping(PATH)
    ShoppingCartDto get(@RequestParam String username);

    @PutMapping(PATH)
    ShoppingCartDto add(@RequestParam String username, @RequestBody Map<UUID, Long> body);

    @DeleteMapping(PATH)
    void deactivate(@RequestParam String username);

    @PostMapping(PATH + "/remove")
    ShoppingCartDto remove(@RequestParam String username, @RequestBody List<UUID> productIds);

    @PostMapping(PATH + "/change-quantity")
    ShoppingCartDto changeQuantity(@RequestParam String username, @RequestBody ChangeProductQuantityRequest request);
}

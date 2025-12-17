package ru.yandex.practicum.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartApi {

    String PATH = "/api/v1/shopping-cart";

    @GetMapping(PATH)
    ShoppingCartDto get(@RequestParam @NotBlank(message = "Заполните имя") String username);

    @PutMapping(PATH)
    ShoppingCartDto add(@RequestParam @NotBlank(message = "Заполните имя") String username,
                        @RequestBody Map<UUID, Long> body);

    @DeleteMapping(PATH)
    void deactivate(@RequestParam @NotBlank(message = "Заполните имя") String username);

    @PostMapping(PATH + "/remove")
    ShoppingCartDto remove(@RequestParam @NotBlank(message = "Заполните имя") String username,
                           @RequestBody List<UUID> productIds);

    @PostMapping(PATH + "/change-quantity")
    ShoppingCartDto changeQuantity(@RequestParam @NotBlank(message = "Заполните имя") String username,
                                   @RequestBody @Valid ChangeProductQuantityRequest request);
}

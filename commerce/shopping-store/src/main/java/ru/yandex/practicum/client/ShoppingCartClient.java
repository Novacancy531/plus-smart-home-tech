package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.ShoppingCartApi;
import ru.yandex.practicum.api.WarehouseApi;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartClient extends ShoppingCartApi {
}

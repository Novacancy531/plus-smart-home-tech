package ru.yandex.practicum.api;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.util.List;
import java.util.UUID;

public interface OrderApi {

    String PATH = "/api/v1/order";

    @GetMapping(PATH)
    public List<OrderDto> getClientOrders(@RequestParam String username);

    @PutMapping(PATH)
    public OrderDto create(@RequestBody ShoppingCartDto cart, @RequestParam String username);

    @PostMapping(PATH + "/assembly")
    public OrderDto assembly(@RequestBody UUID orderId);

    @PostMapping(PATH + "/calculate/delivery")
    public OrderDto deliveryCost(@RequestBody UUID orderId);

    @PostMapping(PATH + "/calculate/total")
    public OrderDto totalCost(@RequestBody UUID orderId);

    @PostMapping(PATH + "/payment")
    public OrderDto payment(@RequestBody UUID orderId);

    @PostMapping(PATH + "/payment/failed")
    public OrderDto paymentFailed(@RequestBody UUID orderId);

    @PostMapping(PATH + "/delivery")
    public OrderDto delivery(@RequestBody UUID orderId);

    @PostMapping(PATH + "/delivery/failed")
    public OrderDto deliveryFailed(@RequestBody UUID orderId);

    @PostMapping(PATH + "/completed")
    public OrderDto completed(@RequestBody UUID orderId);
}

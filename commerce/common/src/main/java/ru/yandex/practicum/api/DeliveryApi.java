package ru.yandex.practicum.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryApi {

    String PATH = "/api/v1/delivery";

    @PutMapping(PATH)
    DeliveryDto planDelivery(@RequestBody @Valid DeliveryDto delivery);

    @PostMapping(PATH + "/picked")
    void deliveryPicked(@RequestBody UUID orderId);

    @PostMapping(PATH + "/successful")
    void deliverySuccessful(@RequestBody UUID orderId);

    @PostMapping(PATH + "/failed")
    void deliveryFailed(@RequestBody UUID orderId);

    @PostMapping(PATH + "/cost")
    BigDecimal deliveryCost(@RequestBody @Valid OrderDto order);
}

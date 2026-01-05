package ru.yandex.practicum.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentApi {

    String PATH = "/api/v1/payment";

    @PostMapping(PATH + "/productCost")
    BigDecimal productCost(@RequestBody @Valid OrderDto order);

    @PostMapping(PATH + "/totalCost")
    BigDecimal totalCost(@RequestBody @Valid OrderDto order);

    @PostMapping(PATH)
    PaymentDto payment(@RequestBody @Valid OrderDto order);

    @PostMapping(PATH + "/refund")
    void paymentSuccess(@RequestBody UUID paymentId);

    @PostMapping(PATH + "/failed")
    void paymentFailed(@RequestBody UUID paymentId);
}

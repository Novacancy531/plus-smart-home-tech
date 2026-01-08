package ru.yandex.practicum.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.api.PaymentApi;
import ru.yandex.practicum.domain.service.PaymentService;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    @PostMapping("/productCost")
    public BigDecimal productCost(@RequestBody @Valid OrderDto order) {
        return paymentService.calculateProductCost(order);
    }

    @PostMapping("/totalCost")
    public BigDecimal totalCost(@RequestBody @Valid OrderDto order) {
        return paymentService.calculateTotalCost(order);
    }

    @PostMapping
    public PaymentDto payment(@RequestBody @Valid OrderDto order) {
        return paymentService.createPayment(order);
    }

    @PostMapping("/refund")
    public void paymentSuccess(@RequestBody UUID paymentId) {
        paymentService.markSuccess(paymentId);
    }

    @PostMapping("/failed")
    public void paymentFailed(@RequestBody UUID paymentId) {
        paymentService.markFailed(paymentId);
    }
}

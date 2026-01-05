package ru.yandex.practicum.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.mapper.PaymentMapper;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dal.entity.Payment;
import ru.yandex.practicum.dal.repository.PaymentRepository;
import ru.yandex.practicum.domain.exception.NoOrderFoundException;
import ru.yandex.practicum.domain.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.payment.enums.PaymentStatus;
import ru.yandex.practicum.dto.store.ProductDto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;
    private final PaymentCalculator calculator;
    private final PaymentMapper paymentMapper;

    public BigDecimal calculateProductCost(OrderDto order) {
        validateOrderForCost(order);

        Map<UUID, Long> products = order.getShoppingCartDto().getProducts();

        BigDecimal sum = BigDecimal.ZERO;
        for (Map.Entry<UUID, Long> e : products.entrySet()) {
            UUID productId = e.getKey();
            long qty = e.getValue() == null ? 0 : e.getValue();
            if (qty <= 0) continue;

            ProductDto product = shoppingStoreClient.getProduct(productId);
            if (product == null || product.getPrice() == null) {
                throw new NotEnoughInfoInOrderToCalculateException("Не удалось получить цену товара: " + productId);
            }

            sum = sum.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
        }
        return sum;
    }

    public BigDecimal calculateTotalCost(OrderDto order) {
        validateOrderForCost(order);

        BigDecimal productCost = calculateProductCost(order);
        BigDecimal delivery = nullOrZero(order.getDeliveryPrice());

        return calculator.totalCost(productCost, delivery);
    }

    @Transactional
    public PaymentDto createPayment(OrderDto order) {
        validateOrderForPayment(order);

        BigDecimal productCost = calculateProductCost(order);
        BigDecimal delivery = nullOrZero(order.getDeliveryPrice());
        BigDecimal fee = calculator.vatFromProducts(productCost);
        BigDecimal total = productCost.add(fee).add(delivery);

        Payment payment = paymentMapper.fromCalculated(
                order.getOrderId(),
                productCost,
                delivery,
                fee,
                total,
                PaymentStatus.PENDING
        );

        Payment saved = paymentRepository.save(payment);

        return paymentMapper.toDto(saved);
    }

    @Transactional
    public void markSuccess(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("Платёж не найден: " + paymentId));

        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        orderClient.payment(payment.getOrderId());
    }

    @Transactional
    public void markFailed(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("Платёж не найден: " + paymentId));

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

       orderClient.paymentFailed(payment.getOrderId());
    }

    private void validateOrderForCost(OrderDto order) {
        if (order == null || order.getOrderId() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("orderId обязателен");
        }

        ShoppingCartDto cart = order.getShoppingCartDto();
        if (cart == null || cart.getShoppingCartId() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("shoppingCartDto.shoppingCartId обязателен");
        }

        if (cart.getProducts() == null || cart.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("shoppingCartDto.products обязателен");
        }
    }

    private void validateOrderForPayment(OrderDto order) {
        validateOrderForCost(order);

        if (order.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("deliveryPrice обязателен для формирования оплаты");
        }
    }

    private BigDecimal nullOrZero(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}

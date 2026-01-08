package ru.yandex.practicum.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.yandex.practicum.api.mapper.PaymentMapper;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dal.entity.Payment;
import ru.yandex.practicum.dal.repository.PaymentRepository;
import ru.yandex.practicum.domain.exception.NoOrderFoundException;
import ru.yandex.practicum.domain.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.payment.enums.PaymentStatus;
import ru.yandex.practicum.dto.store.ProductDto;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;
    private final PaymentCalculator calculator;
    private final PaymentMapper paymentMapper;
    private final PlatformTransactionManager transactionManager;

    public BigDecimal calculateProductCost(OrderDto order) {
        log.info("Calculate product cost started, orderId={}",
                order != null ? order.getOrderId() : null);

        validateOrderForCost(order);

        Map<UUID, Long> products = order.getShoppingCartDto().getProducts();
        BigDecimal sum = BigDecimal.ZERO;

        for (Map.Entry<UUID, Long> e : products.entrySet()) {
            UUID productId = e.getKey();
            long qty = e.getValue() == null ? 0 : e.getValue();
            if (qty <= 0) {
                log.debug("Skip product with zero qty, productId={}", productId);
                continue;
            }

            ProductDto product = shoppingStoreClient.getProduct(productId);
            if (product == null || product.getPrice() == null) {
                log.error("Product price not found, productId={}", productId);
                throw new NotEnoughInfoInOrderToCalculateException(
                        "Не удалось получить цену товара: " + productId
                );
            }

            BigDecimal cost = product.getPrice().multiply(BigDecimal.valueOf(qty));
            log.debug("Product cost calculated, productId={}, qty={}, cost={}",
                    productId, qty, cost);

            sum = sum.add(cost);
        }

        log.info("Product cost calculated, orderId={}, sum={}",
                order.getOrderId(), sum);

        return sum;
    }

    public BigDecimal calculateTotalCost(OrderDto order) {
        log.info("Calculate total cost started, orderId={}",
                order != null ? order.getOrderId() : null);

        validateOrderForCost(order);

        BigDecimal productCost = calculateProductCost(order);
        BigDecimal delivery = nullOrZero(order.getDeliveryPrice());
        BigDecimal total = calculator.totalCost(productCost, delivery);

        log.info("Total cost calculated, orderId={}, total={}",
                order.getOrderId(), total);

        return total;
    }

    public PaymentDto createPayment(OrderDto order) {
        log.info("Create payment started, orderId={}", order.getOrderId());

        validateOrderForPayment(order);

        BigDecimal productCost = calculateProductCost(order);
        BigDecimal delivery = nullOrZero(order.getDeliveryPrice());
        BigDecimal fee = calculator.vatFromProducts(productCost);
        BigDecimal total = productCost.add(fee).add(delivery);

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus tx = transactionManager.getTransaction(def);
        try {
            Payment payment = paymentMapper.fromCalculated(
                    order.getOrderId(),
                    productCost,
                    delivery,
                    fee,
                    total,
                    PaymentStatus.PENDING
            );

            Payment saved = paymentRepository.save(payment);
            transactionManager.commit(tx);

            PaymentDto result = paymentMapper.toDto(saved);

            log.info("Payment created, paymentId={}, orderId={}, total={}, status={}",
                    saved.getPaymentId(), saved.getOrderId(), saved.getTotalPayment(), saved.getStatus());

            return result;
        } catch (RuntimeException e) {
            transactionManager.rollback(tx);
            log.error("Payment create failed, orderId={}", order.getOrderId(), e);
            throw e;
        }
    }

    @Transactional
    public void markSuccess(UUID paymentId) {
        log.info("Mark payment SUCCESS, paymentId={}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.error("Payment not found, paymentId={}", paymentId);
                    return new NoOrderFoundException("Платёж не найден: " + paymentId);
                });

        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        log.info("Payment marked SUCCESS, orderId={}", payment.getOrderId());
        orderClient.payment(payment.getOrderId());
    }

    @Transactional
    public void markFailed(UUID paymentId) {
        log.warn("Mark payment FAILED, paymentId={}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.error("Payment not found, paymentId={}", paymentId);
                    return new NoOrderFoundException("Платёж не найден: " + paymentId);
                });

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        log.info("Payment marked FAILED, orderId={}", payment.getOrderId());
        orderClient.paymentFailed(payment.getOrderId());
    }

    private void validateOrderForCost(OrderDto order) {
        log.debug("Validate order for cost, order={}", order);

        if (order == null || order.getOrderId() == null) {
            log.error("orderId is null");
            throw new NotEnoughInfoInOrderToCalculateException("orderId обязателен");
        }

        ShoppingCartDto cart = order.getShoppingCartDto();
        if (cart == null || cart.getShoppingCartId() == null) {
            log.error("shoppingCartId is null, orderId={}", order.getOrderId());
            throw new NotEnoughInfoInOrderToCalculateException(
                    "shoppingCartDto.shoppingCartId обязателен"
            );
        }

        if (cart.getProducts() == null || cart.getProducts().isEmpty()) {
            log.error("products is empty, orderId={}", order.getOrderId());
            throw new NotEnoughInfoInOrderToCalculateException(
                    "shoppingCartDto.products обязателен"
            );
        }
    }

    private void validateOrderForPayment(OrderDto order) {
        log.debug("Validate order for payment, orderId={}",
                order != null ? order.getOrderId() : null);

        validateOrderForCost(order);

        if (order.getDeliveryPrice() == null) {
            log.error("deliveryPrice is null, orderId={}", order.getOrderId());
            throw new NotEnoughInfoInOrderToCalculateException(
                    "deliveryPrice обязателен для формирования оплаты"
            );
        }
    }

    private BigDecimal nullOrZero(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}

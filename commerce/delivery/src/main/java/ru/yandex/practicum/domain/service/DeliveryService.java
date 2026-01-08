package ru.yandex.practicum.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.mapper.DeliveryMapper;
import ru.yandex.practicum.dal.entity.Delivery;
import ru.yandex.practicum.dal.repository.DeliveryRepository;
import ru.yandex.practicum.domain.exception.NoDeliveryFoundException;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.enums.DeliveryState;
import ru.yandex.practicum.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final DeliveryCostCalculator calculator;

    @Transactional
    public DeliveryDto planDelivery(DeliveryDto dto) {
        log.info("Plan delivery started, dto={}", dto);

        Delivery delivery = deliveryMapper.toEntity(dto);
        delivery.setDeliveryState(DeliveryState.CREATED);

        Delivery saved = deliveryRepository.save(delivery);
        DeliveryDto result = deliveryMapper.toDto(saved);

        log.info("Plan delivery finished, deliveryId={}, state={}",
                saved.getDeliveryId(), saved.getDeliveryState());

        return result;
    }

    @Transactional
    public void deliveryPicked(UUID orderId) {
        log.info("Delivery picked, orderId={}", orderId);

        Delivery delivery = getByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);

        log.info("Delivery state updated to IN_PROGRESS, deliveryId={}",
                delivery.getDeliveryId());
    }

    @Transactional
    public void deliverySuccessful(UUID orderId) {
        log.info("Delivery successful, orderId={}", orderId);

        Delivery delivery = getByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        log.info("Delivery state updated to DELIVERED, deliveryId={}",
                delivery.getDeliveryId());
    }

    @Transactional
    public void deliveryFailed(UUID orderId) {
        log.warn("Delivery failed, orderId={}", orderId);

        Delivery delivery = getByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        log.info("Delivery state updated to FAILED, deliveryId={}",
                delivery.getDeliveryId());
    }

    @Transactional(readOnly = true)
    public BigDecimal deliveryCost(OrderDto order) {
        log.info("Calculate delivery cost started, order={}", order);

        if (order == null || order.getOrderId() == null) {
            log.error("Order or orderId is null");
            throw new NoDeliveryFoundException("orderId обязателен для расчёта доставки");
        }

        Delivery delivery = getByOrderId(order.getOrderId());
        BigDecimal cost = calculator.calculate(delivery, order);

        log.info("Delivery cost calculated, orderId={}, cost={}",
                order.getOrderId(), cost);

        return cost;
    }

    @Transactional(readOnly = true)
    public UUID getDeliveryUUID(UUID orderId) {
        log.info("Get delivery UUID by orderId={}", orderId);

        UUID deliveryId = deliveryRepository.findDeliveryUUIDByOrderUUID(orderId)
                .orElseThrow(() -> {
                    log.error("Delivery not found for orderId={}", orderId);
                    return new NoDeliveryFoundException("Не найдена доставка для заказа: " + orderId);
                });

        log.info("Found delivery UUID={}, orderId={}", deliveryId, orderId);
        return deliveryId;
    }

    private Delivery getByOrderId(UUID orderId) {
        log.debug("Find delivery by orderId={}", orderId);

        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.error("Delivery not found for orderId={}", orderId);
                    return new NoDeliveryFoundException("Не найдена доставка для заказа: " + orderId);
                });
    }
}

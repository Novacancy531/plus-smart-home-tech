package ru.yandex.practicum.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.mapper.OrderMapper;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dal.entity.Order;
import ru.yandex.practicum.dal.repository.OrderRepository;
import ru.yandex.practicum.domain.exception.NoOrderFoundException;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.enums.OrderState;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username) {
        log.info("Getting orders for user={}", username);

        List<OrderDto> orders = orderRepository.findAllByUsername(username)
                .stream()
                .map(orderMapper::toDto)
                .toList();

        log.info("Found {} orders for user={}", orders.size(), username);
        return orders;
    }

    @Transactional
    public OrderDto createNewOrder(ShoppingCartDto cart, String username) {
        log.info("Creating new order for user={}, cartId={}", username, cart.getShoppingCartId());

        try {
            log.debug("Checking availability in warehouse, cart={}", cart);
            warehouseClient.checkAvailability(cart);

            Order order = orderMapper.fromCart(username, cart);
            Order saved = orderRepository.save(order);

            log.info("Order created successfully, orderId={}", saved.getOrderId());
            return orderMapper.toDto(saved);
        } catch (Exception e) {
            log.error("Failed to create order for user={}, cart={}", username, cart, e);
            throw e;
        }
    }

    @Transactional
    public OrderDto assembly(UUID orderId) {
        log.info("Assembling order, orderId={}", orderId);

        Order order = get(orderId);
        order.setState(OrderState.ASSEMBLED);

        Order saved = orderRepository.save(order);
        log.info("Order assembled, orderId={}", orderId);

        return orderMapper.toDto(saved);
    }

    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Calculating delivery cost, orderId={}", orderId);

        try {
            Order order = get(orderId);
            BigDecimal delivery = deliveryClient.deliveryCost(orderMapper.toDto(order));
            UUID deliveryUUID = deliveryClient.deliveryUUID(orderId);

            order.setDeliveryPrice(delivery);
            order.setDeliveryId(deliveryUUID);
            Order saved = orderRepository.save(order);

            log.info("Delivery cost calculated, orderId={}, deliveryPrice={}", orderId, delivery);
            return orderMapper.toDto(saved);
        } catch (Exception e) {
            log.error("Failed to calculate delivery cost, orderId={}", orderId, e);
            throw e;
        }
    }

    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Calculating total cost, orderId={}", orderId);

        try {
            Order order = get(orderId);
            OrderDto dto = orderMapper.toDto(order);

            BigDecimal productPrice = paymentClient.productCost(dto);
            BigDecimal totalPrice = paymentClient.totalCost(dto);

            order.setProductPrice(productPrice);
            order.setTotalPrice(totalPrice);

            Order saved = orderRepository.save(order);

            log.info(
                    "Total cost calculated, orderId={}, productPrice={}, totalPrice={}",
                    orderId, productPrice, totalPrice
            );

            return orderMapper.toDto(saved);
        } catch (Exception e) {
            log.error("Failed to calculate total cost, orderId={}", orderId, e);
            throw e;
        }
    }

    @Transactional
    public OrderDto payment(UUID orderId) {
        log.info("Processing payment, orderId={}", orderId);

        try {
            Order order = get(orderId);
            PaymentDto payment = paymentClient.payment(orderMapper.toDto(order));

            order.setPaymentId(payment.getPaymentId());
            order.setState(OrderState.PAID);

            Order saved = orderRepository.save(order);

            log.info(
                    "Payment successful, orderId={}, paymentId={}",
                    orderId, payment.getPaymentId()
            );

            return orderMapper.toDto(saved);
        } catch (Exception e) {
            log.error("Payment failed, orderId={}", orderId, e);
            throw e;
        }
    }

    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        log.warn("Marking payment as failed, orderId={}", orderId);

        Order order = get(orderId);
        order.setState(OrderState.PAYMENT_FAILED);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto delivery(UUID orderId) {
        log.info("Marking order as delivered, orderId={}", orderId);

        Order order = get(orderId);
        order.setState(OrderState.DELIVERED);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        log.warn("Marking delivery as failed, orderId={}", orderId);

        Order order = get(orderId);
        order.setState(OrderState.DELIVERY_FAILED);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto complete(UUID orderId) {
        log.info("Completing order, orderId={}", orderId);

        Order order = get(orderId);
        order.setState(OrderState.COMPLETED);

        return orderMapper.toDto(orderRepository.save(order));
    }

    private Order get(UUID orderId) {
        log.debug("Fetching order by id={}", orderId);

        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found, orderId={}", orderId);
                    return new NoOrderFoundException("Не найден заказ: " + orderId);
                });
    }
}

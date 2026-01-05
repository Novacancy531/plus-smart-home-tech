package ru.yandex.practicum.domain.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dal.entity.Delivery;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DeliveryCostCalculator {

    public BigDecimal calculate(Delivery delivery, OrderDto order) {
        BookedProductsDto booked = order.getBookedProductsDto();

        double weight = booked == null ? 0.0 : booked.getDeliveryWeight();
        double volume = booked == null ? 0.0 : booked.getDeliveryVolume();
        boolean fragile = booked != null && booked.isFragile();

        BigDecimal sum = BigDecimal.ZERO;

        sum = sum.add(BigDecimal.valueOf(50));

        sum = sum.add(BigDecimal.valueOf(weight).multiply(BigDecimal.valueOf(2.0)));
        sum = sum.add(BigDecimal.valueOf(volume).multiply(BigDecimal.valueOf(5.0)));

        if (fragile) {
            sum = sum.add(BigDecimal.valueOf(100));
        }

        sum = sum.add(addressFactor(delivery));

        return sum.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal addressFactor(Delivery d) {
        BigDecimal k = BigDecimal.ZERO;

        if (d.getFromAddress() == null || d.getToAddress() == null) return k;

        if (!eq(d.getFromAddress().getCountry(), d.getToAddress().getCountry())) k = k.add(BigDecimal.valueOf(200));
        if (!eq(d.getFromAddress().getCity(), d.getToAddress().getCity())) k = k.add(BigDecimal.valueOf(100));
        if (!eq(d.getFromAddress().getStreet(), d.getToAddress().getStreet())) k = k.add(BigDecimal.valueOf(20));

        return k;
    }

    private boolean eq(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equalsIgnoreCase(b);
    }
}

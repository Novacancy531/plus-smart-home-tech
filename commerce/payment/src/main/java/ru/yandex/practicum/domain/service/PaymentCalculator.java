package ru.yandex.practicum.domain.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PaymentCalculator {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.10");

    public BigDecimal vatFromProducts(BigDecimal productCost) {
        if (productCost == null) return BigDecimal.ZERO;
        return productCost.multiply(VAT_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal totalCost(BigDecimal productCost, BigDecimal deliveryCost) {
        var products = nullOrZero(productCost);
        var delivery = nullOrZero(deliveryCost);
        var vat = vatFromProducts(products);
        return products.add(vat).add(delivery).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal nullOrZero(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}

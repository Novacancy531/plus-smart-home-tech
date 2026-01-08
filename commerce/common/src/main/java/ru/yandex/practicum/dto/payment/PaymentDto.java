package ru.yandex.practicum.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentDto {

    @NotNull(message = "paymentId обязателен")
    UUID paymentId;

    @NotNull(message = "totalPayment обязателен")
    @PositiveOrZero(message = "totalPayment не может быть отрицательным")
    BigDecimal totalPayment;

    @NotNull(message = "deliveryTotal обязателен")
    @PositiveOrZero(message = "deliveryTotal не может быть отрицательным")
    BigDecimal deliveryTotal;

    @NotNull(message = "feeTotal обязателен")
    @PositiveOrZero(message = "feeTotal не может быть отрицательным")
    BigDecimal feeTotal;
}

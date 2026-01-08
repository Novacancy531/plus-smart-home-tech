package ru.yandex.practicum.dto.delivery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.delivery.enums.DeliveryState;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryDto {

    UUID deliveryId;

    @Valid
    @NotNull(message = "fromAddress обязателен")
    AddressDto fromAddress;

    @Valid
    @NotNull(message = "toAddress обязателен")
    AddressDto toAddress;

    @NotNull(message = "orderId обязателен")
    UUID orderId;

    DeliveryState deliveryState;
}

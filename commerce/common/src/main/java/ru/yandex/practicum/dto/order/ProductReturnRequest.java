package ru.yandex.practicum.dto.order;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductReturnRequest {
    UUID orderId;
    Map<UUID, Long> products;
}

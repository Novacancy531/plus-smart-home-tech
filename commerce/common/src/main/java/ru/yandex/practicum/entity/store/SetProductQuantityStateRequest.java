package ru.yandex.practicum.entity.store;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.entity.store.enums.QuantityState;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SetProductQuantityStateRequest {

    @NotNull
    UUID productId;
    @NotNull
    QuantityState quantityState;
}

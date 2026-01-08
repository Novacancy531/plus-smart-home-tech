package ru.yandex.practicum.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {

     @NotNull(message = "orderId обязателен")
     UUID orderId;

     @Valid
     @NotNull(message = "shoppingCartDto обязателен")
     ShoppingCartDto shoppingCartDto;

     UUID paymentId;

     UUID deliveryId;

     @NotNull(message = "state обязателен")
     String state;

     @Valid
     @NotNull(message = "bookedProductsDto обязателен")
     BookedProductsDto bookedProductsDto;

     @PositiveOrZero(message = "totalPrice не может быть отрицательной")
     BigDecimal totalPrice;

     @PositiveOrZero(message = "deliveryPrice не может быть отрицательной")
     BigDecimal deliveryPrice;

     @PositiveOrZero(message = "productPrice не может быть отрицательной")
     BigDecimal productPrice;
}

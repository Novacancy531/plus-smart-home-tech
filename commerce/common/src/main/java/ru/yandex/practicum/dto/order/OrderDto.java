package ru.yandex.practicum.dto.order;

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
     UUID orderId;
     ShoppingCartDto shoppingCartDto;
     UUID paymentId;
     UUID deliveryId;
     String state;
     BookedProductsDto bookedProductsDto;
     BigDecimal totalPrice;
     BigDecimal deliveryPrice;
     BigDecimal productPrice;
}

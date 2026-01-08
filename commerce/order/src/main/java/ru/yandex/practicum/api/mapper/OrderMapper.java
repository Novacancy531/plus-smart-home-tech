package ru.yandex.practicum.api.mapper;

import org.mapstruct.*;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.dal.entity.Order;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OrderMapper {

    @Mapping(target = "shoppingCartDto.shoppingCartId", source = "shoppingCartId")
    @Mapping(target = "shoppingCartDto.products", source = "products")
    @Mapping(target = "bookedProductsDto.deliveryWeight", source = "deliveryWeight")
    @Mapping(target = "bookedProductsDto.deliveryVolume", source = "deliveryVolume")
    @Mapping(target = "bookedProductsDto.fragile", source = "fragile")
    @Mapping(target = "state", expression = "java(order.getState().name())")
    OrderDto toDto(Order order);

    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "deliveryId", ignore = true)
    @Mapping(target = "deliveryWeight", ignore = true)
    @Mapping(target = "deliveryVolume", ignore = true)
    @Mapping(target = "fragile", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "deliveryPrice", ignore = true)
    @Mapping(target = "productPrice", ignore = true)
    @Mapping(target = "state", expression = "java(ru.yandex.practicum.dto.order.enums.OrderState.NEW)")
    Order fromCart(String username, ShoppingCartDto shoppingCartDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Order target, Order source);
}

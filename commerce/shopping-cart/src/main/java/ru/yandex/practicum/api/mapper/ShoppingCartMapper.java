package ru.yandex.practicum.api.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dal.entity.ShoppingCart;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    ShoppingCartDto toDto(ShoppingCart entity);
}

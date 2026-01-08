package ru.yandex.practicum.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.dal.entity.Delivery;
import ru.yandex.practicum.dal.entity.DeliveryAddress;
import ru.yandex.practicum.dto.delivery.AddressDto;
import ru.yandex.practicum.dto.delivery.DeliveryDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DeliveryMapper {

    @Mapping(target = "fromAddress", source = "fromAddress")
    @Mapping(target = "toAddress", source = "toAddress")
    DeliveryDto toDto(Delivery delivery);

    @Mapping(target = "deliveryId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Delivery toEntity(DeliveryDto dto);

    AddressDto toDto(DeliveryAddress address);

    DeliveryAddress toEntity(AddressDto dto);
}

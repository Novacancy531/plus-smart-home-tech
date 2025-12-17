package ru.yandex.practicum.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.dal.entity.WarehouseProduct;
import ru.yandex.practicum.dto.warehouse.WarehouseProductDto;

@Mapper(
        componentModel = "spring",
        uses = DimensionMapper.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface WarehouseProductMapper {

    @Mapping(target = "dimension", source = "dimensionDto")
    WarehouseProduct toEntity(WarehouseProductDto warehouseProductDto);

    @Mapping(target = "dimensionDto", source = "dimension")
    WarehouseProductDto toDto(WarehouseProduct warehouseProduct);
}

package ru.yandex.practicum.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.dal.entity.Dimension;
import ru.yandex.practicum.entity.warehouse.DimensionDto;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DimensionMapper {

    @Mapping(target = "productId", source = "productId")
    Dimension toEntity(DimensionDto dimensionDto, UUID productId);

    @Mapping(target = "width", source = "width")
    @Mapping(target = "height", source = "height")
    @Mapping(target = "depth", source = "depth")
    DimensionDto toDto(Dimension dimension);
}

package ru.yandex.practicum.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.dal.entity.Dimension;
import ru.yandex.practicum.dto.warehouse.DimensionDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DimensionMapper {

    @Mapping(target = "id", ignore = true)
    Dimension toEntity(DimensionDto dimensionDto);

    DimensionDto toDto(Dimension dimension);
}

package ru.yandex.practicum.api.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.dal.entity.Product;
import ru.yandex.practicum.entity.store.ProductDto;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductMapper {

    ProductDto toDto(Product entity);

    Product toEntity(ProductDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Product entity, ProductDto dto);
}

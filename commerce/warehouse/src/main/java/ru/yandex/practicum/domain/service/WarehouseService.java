package ru.yandex.practicum.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.mapper.DimensionMapper;
import ru.yandex.practicum.api.mapper.WarehouseProductMapper;
import ru.yandex.practicum.dal.entity.WarehouseProduct;
import ru.yandex.practicum.dal.repository.WarehouseRepository;
import ru.yandex.practicum.domain.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.domain.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.domain.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.delivery.AddressDto;
import ru.yandex.practicum.dto.warehouse.*;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseService {

    private final WarehouseRepository repository;
    private final DimensionMapper dimensionMapper;
    private final WarehouseProductMapper warehouseProductMapper;

    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];

    public WarehouseProductDto newProductInWarehouse(NewProductInWarehouseRequest request) {
        UUID productId = request.getProductId();

        if (repository.existsById(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Ошибка, товар с таким описанием уже зарегистрирован на складе", Map.of("productId", productId));
        }

        var dimension = dimensionMapper.toEntity(request.getDimension());

        var warehouseProduct = WarehouseProduct.builder()
                .productId(productId)
                .fragile(request.isFragile())
                .dimension(dimension)
                .weight(request.getWeight())
                .quantity(0L)
                .build();

        repository.save(warehouseProduct);

        return warehouseProductMapper.toDto(warehouseProduct);
    }

    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        UUID productId = request.getProductId();

        var warehouseProduct = repository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Нет информации о товаре на складе",
                        Map.of("productId", productId)));

        warehouseProduct.setQuantity(warehouseProduct.getQuantity() + request.getQuantity());
    }

    public AddressDto getWarehouseAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    @Transactional(readOnly = true)
    public void checkAvailability(ShoppingCartDto shoppingCartDto) {
        var warehouseProducts = getWarehouseProducts(shoppingCartDto.getProducts().keySet());
        var outOfStockList = new HashMap<String, Object>();

        for (Map.Entry<UUID, Long> entry : shoppingCartDto.getProducts().entrySet()) {
            long required = entry.getValue();
            long available = Optional.ofNullable(warehouseProducts.get(entry.getKey()))
                    .map(WarehouseProduct::getQuantity)
                    .orElse(0L);
            long missing = required - available;

            if(missing > 0) {
                outOfStockList.put(entry.getKey().toString(), missing);
            }
        }

        if(!outOfStockList.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточно товаров на складе",
                    outOfStockList);
        }
    }

    private Map<UUID, WarehouseProduct> getWarehouseProducts(Set<UUID> productIds) {
        return  repository.findAllByProductIdIn(productIds)
                .stream()
                .collect(Collectors.toMap(
                        WarehouseProduct::getProductId,
                        Function.identity()
                ));
    }
}

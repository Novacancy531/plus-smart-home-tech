package ru.yandex.practicum.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.mapper.DimensionMapper;
import ru.yandex.practicum.dal.entity.WarehouseProduct;
import ru.yandex.practicum.dal.repository.WarehouseRepository;
import ru.yandex.practicum.domain.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.domain.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.domain.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.entity.cart.ShoppingCartDto;
import ru.yandex.practicum.entity.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.entity.warehouse.AddressDto;
import ru.yandex.practicum.entity.warehouse.BookedProductsDto;
import ru.yandex.practicum.entity.warehouse.NewProductInWarehouseRequest;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseService {

    private final WarehouseRepository repository;
    private final DimensionMapper dimensionMapper;

    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];

    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        UUID productId = request.getProductId();

        if (repository.existsById(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Ошибка, товар с таким описанием уже зарегистрирован на складе"
            );
        }

        var dimension = dimensionMapper.toEntity(request.getDimension(), productId);

        var warehouseProduct = WarehouseProduct.builder()
                .productId(productId)
                .fragile(request.isFragile())
                .dimension(dimension)
                .weight(request.getWeight())
                .quantity(0L)
                .build();

        repository.save(warehouseProduct);
    }

    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        UUID productId = request.getProductId();

        var warehouseProduct = repository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Нет информации о товаре на складе"));

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
    public BookedProductsDto checkAvailability(ShoppingCartDto shoppingCartDto) {
        double deliveryWeight = 0.0;
        double deliveryVolume = 0.0;
        boolean fragile = false;

        for (Map.Entry<UUID, Long> productEntry : shoppingCartDto.getProducts().entrySet()) {
            UUID productId = productEntry.getKey();
            long requiredQuantity = productEntry.getValue();

            var warehouseProduct = repository.findById(productId)
                    .orElseThrow(() -> new ProductInShoppingCartLowQuantityInWarehouse(
                            "Ошибка, товар из корзины не находится в требуемом количестве на складе"
                    ));

            long availableQuantity = warehouseProduct.getQuantity();
            if (availableQuantity < requiredQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Ошибка, товар из корзины не находится в требуемом количестве на складе"
                );
            }

            deliveryWeight += calculateDeliveryWeight(warehouseProduct.getWeight(), requiredQuantity);

            var dimension = warehouseProduct.getDimension();
            deliveryVolume += calculateDeliveryVolume(dimension.getWidth(), dimension.getHeight(), dimension.getDepth(),
                    requiredQuantity);

            fragile = fragile || warehouseProduct.isFragile();
        }

        return BookedProductsDto.builder()
                .deliveryWeight(deliveryWeight)
                .deliveryVolume(deliveryVolume)
                .fragile(fragile)
                .build();
    }

    private double calculateDeliveryWeight(double weight, long quantity) {
        return weight * quantity;
    }

    private double calculateDeliveryVolume(double width, double height, double depth, long quantity) {
        return (width * height * depth) * quantity;
    }
}

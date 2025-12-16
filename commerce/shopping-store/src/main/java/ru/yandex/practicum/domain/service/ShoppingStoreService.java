package ru.yandex.practicum.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.mapper.ProductMapper;
import ru.yandex.practicum.dal.entity.Product;
import ru.yandex.practicum.dal.repository.ShoppingStoreRepository;
import ru.yandex.practicum.domain.exception.ProductNotFoundException;
import ru.yandex.practicum.entity.store.ProductDto;
import ru.yandex.practicum.entity.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.entity.store.enums.ProductCategory;
import ru.yandex.practicum.entity.store.enums.ProductState;
import ru.yandex.practicum.entity.store.enums.QuantityState;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingStoreService {

    private final ShoppingStoreRepository repository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductCategory category, int page, int size) {
        return repository.findAllByProductCategory(category, PageRequest.of(page, size))
                .map(productMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        var product = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return productMapper.toDto(product);
    }

    public ProductDto createProduct(ProductDto dto) {
        Product entity = productMapper.toEntity(dto);

        defaultsValues(entity);

        Product saved = repository.save(entity);
        return productMapper.toDto(saved);
    }

    public ProductDto updateProduct(ProductDto dto) {
        var productId = dto.getProductId();
        if (productId == null) {
            throw new IllegalArgumentException("productId обязателен для обновления товара");
        }

        Product existing = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        productMapper.updateEntity(existing, dto);

        defaultsValues(existing);

        Product saved = repository.save(existing);
        return productMapper.toDto(saved);
    }

    public boolean removeProduct(UUID productId) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.setProductState(ProductState.DEACTIVATE);
        repository.save(product);

        return true;
    }

    public ProductDto setQuantityState(SetProductQuantityStateRequest request) {
        var product = repository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        product.setQuantityState(request.getQuantityState());

        var saved = repository.save(product);
        return productMapper.toDto(saved);
    }

    private void defaultsValues(Product product) {
        if (product.getProductState() == null) {
            product.setProductState(ProductState.ACTIVE);
        }
        if (product.getQuantityState() == null) {
            product.setQuantityState(QuantityState.ENDED);
        }
    }
}

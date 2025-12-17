package ru.yandex.practicum.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.mapper.ProductMapper;
import ru.yandex.practicum.dal.entity.Product;
import ru.yandex.practicum.dal.repository.ShoppingStoreRepository;
import ru.yandex.practicum.domain.exception.ProductNotFoundException;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.enums.ProductCategory;
import ru.yandex.practicum.dto.store.enums.ProductState;
import ru.yandex.practicum.dto.store.enums.QuantityState;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingStoreService {

    private final ShoppingStoreRepository repository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        return repository.findAllByProductCategory(category, pageable)
                .map(productMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        return productMapper.toDto(repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId)));
    }

    public ProductDto createProduct(ProductDto dto) {
        var product = repository.save(productMapper.toEntity(dto));
        return productMapper.toDto(product);
    }

    public ProductDto updateProduct(ProductDto dto) {
        var productId = dto.getProductId();
        if (productId == null) {
            throw new IllegalArgumentException("productId обязателен для обновления товара");
        }

        Product existing = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        productMapper.updateEntity(existing, dto);

        Product saved = repository.save(existing);
        return productMapper.toDto(saved);
    }

    public boolean removeProduct(UUID productId) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.setProductState(ProductState.DEACTIVATE);
        return true;
    }

    public boolean setQuantityState(UUID productId, QuantityState quantityState) {
        var product = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.setQuantityState(quantityState);
        return true;
    }
}

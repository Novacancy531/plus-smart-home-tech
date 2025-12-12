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
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.model.SetProductQuantityStateRequest;
import ru.yandex.practicum.model.enums.ProductCategory;
import ru.yandex.practicum.model.enums.ProductState;
import ru.yandex.practicum.model.enums.QuantityState;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingStoreService {

    private final ShoppingStoreRepository repository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        Page<Product> page = repository.findAllByProductCategory(category, pageable);
        return page.map(productMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        Product product = repository.findById(productId)
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
        UUID productId = dto.getProductId();
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

    public boolean setQuantityState(SetProductQuantityStateRequest request) {
        UUID productId = request.getProductId();

        Product product = repository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.setQuantityState(request.getQuantityState());

        repository.save(product);
        return true;
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

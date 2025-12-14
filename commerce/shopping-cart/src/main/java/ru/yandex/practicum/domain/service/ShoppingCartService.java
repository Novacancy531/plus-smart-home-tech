package ru.yandex.practicum.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.mapper.ShoppingCartMapper;
import ru.yandex.practicum.dal.entity.ShoppingCart;
import ru.yandex.practicum.dal.repository.ShoppingCartRepository;
import ru.yandex.practicum.domain.exception.CartIsDeactivatedException;
import ru.yandex.practicum.domain.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.domain.exception.NotAuthorizedUserException;
import ru.yandex.practicum.entity.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.entity.cart.ShoppingCartDto;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartService {

    private final ShoppingCartRepository repository;
    private final ShoppingCartMapper mapper;

    @Transactional(readOnly = true)
    public ShoppingCartDto getShoppingCart(String username) {
        validateUsername(username);

        var cart = getCart(username);

        return mapper.toDto(cart);
    }

    public ShoppingCartDto addProducts(String username, Map<UUID, Long> productsToAdd) {
        validateUsername(username);

        var cart = getCart(username);
        validateCart(cart);

        if (productsToAdd != null) {
            productsToAdd.forEach((productId, quantity) -> {
                if (productId != null && quantity != null && quantity > 0) {
                    cart.getProducts().merge(productId, quantity, Long::sum);
                }
            });
        }

        return mapper.toDto(cart);
    }

    public void deactivateCart(String username) {
        validateUsername(username);

        var cart = getCart(username);
        validateCart(cart);
        cart.setActive(false);
    }

    public ShoppingCartDto removeProducts(String username, List<UUID> productIds) {
        validateUsername(username);

        var cart = getCart(username);
        validateCart(cart);

        boolean removed = false;

        if (productIds == null || productIds.isEmpty()) {
            throw new NoProductsInShoppingCartException("Список товаров пуст");
        }

        for (UUID productId : productIds) {
            removed |= (cart.getProducts().remove(productId) != null);
        }

        if (!removed) {
            throw new NoProductsInShoppingCartException("Нет искомых товаров в корзине");
        }

        return mapper.toDto(cart);
    }

    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        validateUsername(username);

        var cart = getCart(username);
        validateCart(cart);

        UUID productId = request.getProductId();
        long newQuantity = request.getNewQuantity();

        if (!cart.getProducts().containsKey(productId)) {
            throw new NoProductsInShoppingCartException("Нет искомых товаров в корзине");
        }

        if (newQuantity <= 0) {
            cart.getProducts().remove(productId);
        } else {
            cart.getProducts().put(productId, newQuantity);
        }

        return mapper.toDto(cart);
    }


    private ShoppingCart createNewCart(String username) {
        ShoppingCart cart = ShoppingCart.builder()
                .username(username)
                .active(true)
                .build();
        return repository.save(cart);
    }

    private ShoppingCart getCart(String username) {
        return repository.findByUsername(username).orElseGet(() -> createNewCart(username));
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
    }

    private void validateCart(ShoppingCart cart) {
        if (!cart.isActive()) {
            throw new CartIsDeactivatedException("Корзина не активна");
        }
    }
}

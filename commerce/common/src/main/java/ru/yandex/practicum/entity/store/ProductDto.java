package ru.yandex.practicum.entity.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.entity.store.enums.ProductCategory;
import ru.yandex.practicum.entity.store.enums.ProductState;
import ru.yandex.practicum.entity.store.enums.QuantityState;

import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {

    UUID productId;

    @NotBlank(message = "Имя продукта не может быть пустым")
    String productName;

    @JsonProperty("description")
    @NotBlank(message = "Описание продукта не может быть пустым")
    String productDescription;

    String imageSrc;

    QuantityState quantityState;

    ProductState productState;

    @NotNull(message = "Категория не может быть пустой")
    ProductCategory productCategory;

    @Positive
    @NotNull
    Double price;
}

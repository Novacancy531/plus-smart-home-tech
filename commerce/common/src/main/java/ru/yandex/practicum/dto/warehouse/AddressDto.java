package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDto {

    @NotBlank(message = "Заполните страну.")
    String country;

    @NotBlank(message = "Заполните город.")
    String city;

    @NotBlank(message = "Заполните улицу.")
    String street;

    @NotBlank(message = "Заполните дом.")
    String house;

    @NotBlank(message = "Заполните квартиру.")
    String flat;
}

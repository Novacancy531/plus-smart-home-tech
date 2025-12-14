package ru.yandex.practicum.entity.warehouse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDto {

    String country;
    String city;
    String street;
    String house;
    String flat;
}

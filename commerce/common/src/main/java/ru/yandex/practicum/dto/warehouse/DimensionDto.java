package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {

    @NotNull(message = "Заполните ширину.")
    double width;

    @NotNull(message = "Заполните высоту.")
    double height;

    @NotNull(message = "Заполните глубину.")
    double depth;
}

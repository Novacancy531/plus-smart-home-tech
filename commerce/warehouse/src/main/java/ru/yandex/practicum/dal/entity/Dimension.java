package ru.yandex.practicum.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "dimensions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dimension {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "width", nullable = false)
    double width;

    @Column(name = "height", nullable = false)
    double height;

    @Column(name = "depth", nullable = false)
    double depth;
}

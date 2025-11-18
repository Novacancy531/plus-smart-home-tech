package ru.yandex.practicum.kafka.telemetry.analyzer.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sensors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sensor {

    @Id
    private String id;

    @Column(name = "hub_id")
    private String hubId;
}

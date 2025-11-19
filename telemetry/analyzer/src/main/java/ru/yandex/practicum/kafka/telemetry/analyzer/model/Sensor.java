package ru.yandex.practicum.kafka.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sensors")
public class Sensor {

    @Id
    private String id;

    @Column(name = "hub_id")
    private String hubId;
}

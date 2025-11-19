package ru.yandex.practicum.kafka.telemetry.analyzer.service.dto;

import lombok.Data;

@Data
public class SensorState {
    private String id;
    private String type;
    private Integer value;
}

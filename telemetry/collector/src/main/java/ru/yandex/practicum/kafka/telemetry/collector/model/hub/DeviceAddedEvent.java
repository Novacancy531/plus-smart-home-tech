package ru.yandex.practicum.kafka.telemetry.collector.model.hub;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.collector.model.enums.DeviceType;

@Getter
@Setter
public class DeviceAddedEvent {
    private String id;
    private DeviceType deviceType;
}

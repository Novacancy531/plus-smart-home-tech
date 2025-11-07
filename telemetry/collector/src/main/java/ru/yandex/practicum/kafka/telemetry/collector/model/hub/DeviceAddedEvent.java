package ru.yandex.practicum.kafka.telemetry.collector.model.hub;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.collector.model.enums.DeviceType;
import ru.yandex.practicum.kafka.telemetry.collector.model.enums.HubEventType;

@Getter
@Setter
public class DeviceAddedEvent extends HubEvent {

    private String id;
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}

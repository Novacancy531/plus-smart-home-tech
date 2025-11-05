package ru.yandex.practicum.kafka.telemetry.collector.model.hub;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScenarioAddedEvent {
    private String name;
    private List<ScenarioCondition> conditions;
    private List<DeviceAction> actions;
}

package ru.yandex.practicum.kafka.telemetry.collector.model.hub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScenarioRemovedEvent {
    private String name;
}

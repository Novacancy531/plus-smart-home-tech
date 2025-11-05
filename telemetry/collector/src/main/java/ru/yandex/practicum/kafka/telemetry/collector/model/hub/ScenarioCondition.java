package ru.yandex.practicum.kafka.telemetry.collector.model.hub;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.collector.model.enums.ConditionOperation;
import ru.yandex.practicum.kafka.telemetry.collector.model.enums.ConditionType;

@Getter
@Setter
public class ScenarioCondition {
    private String sensorId;
    private ConditionType type;
    private ConditionOperation operation;
    private Integer value;
}

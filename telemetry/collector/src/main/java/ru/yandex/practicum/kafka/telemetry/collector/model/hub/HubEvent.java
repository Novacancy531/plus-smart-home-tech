package ru.yandex.practicum.kafka.telemetry.collector.model.hub;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.collector.model.enums.HubEventType;

import java.time.Instant;

@Getter
@Setter
public class HubEvent {

    private String hubId;
    private Instant timestamp = Instant.now();
    private HubEventType type;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
            @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = "DEVICE_REMOVED"),
            @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = "SCENARIO_ADDED"),
            @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = "SCENARIO_REMOVED")
    })
    private Object payload;
}

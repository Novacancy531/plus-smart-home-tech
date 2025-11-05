package ru.yandex.practicum.kafka.telemetry.collector.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.collector.model.hub.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Component
public class HubAvroMapper {

    public static SpecificRecordBase hubToAvro(HubEvent event) {
        HubEventAvro.Builder builder = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp().toEpochMilli());

        switch (event.getType()) {
            case DEVICE_ADDED -> {
                DeviceAddedEvent added = (DeviceAddedEvent) event.getPayload();
                builder.setPayload(DeviceAddedEventAvro.newBuilder()
                        .setId(added.getId())
                        .setType(DeviceTypeAvro.valueOf(added.getDeviceType().name()))
                        .build());
            }
            case DEVICE_REMOVED -> {
                DeviceRemovedEvent removed = (DeviceRemovedEvent) event.getPayload();
                builder.setPayload(DeviceRemovedEventAvro.newBuilder()
                        .setId(removed.getId())
                        .build());
            }
            case SCENARIO_ADDED -> {
                ScenarioAddedEvent scenarioAdded = (ScenarioAddedEvent) event.getPayload();
                builder.setPayload(ScenarioAddedEventAvro.newBuilder()
                        .setName(scenarioAdded.getName())
                        .setConditions(
                                scenarioAdded.getConditions().stream()
                                        .map(cond -> ScenarioConditionAvro.newBuilder()
                                                .setSensorId(cond.getSensorId())
                                                .setType(ConditionTypeAvro.valueOf(cond.getType().name()))
                                                .setOperation(ConditionOperationAvro.valueOf(cond.getOperation().name()))
                                                .setValue(cond.getValue())
                                                .build())
                                        .toList()
                        )
                        .setActions(
                                scenarioAdded.getActions().stream()
                                        .map(act -> DeviceActionAvro.newBuilder()
                                                .setSensorId(act.getSensorId())
                                                .setType(ActionTypeAvro.valueOf(act.getType().name()))
                                                .setValue(act.getValue())
                                                .build())
                                        .toList()
                        )
                        .build());
            }
            case SCENARIO_REMOVED -> {
                ScenarioRemovedEvent scenarioRemoved = (ScenarioRemovedEvent) event.getPayload();
                builder.setPayload(ScenarioRemovedEventAvro.newBuilder()
                        .setName(scenarioRemoved.getName())
                        .build());
            }
            default -> throw new IllegalArgumentException("Unsupported HubEvent type: " + event.getType());
        }

        return builder.build();
    }
}
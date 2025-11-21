package ru.yandex.practicum.kafka.telemetry.collector.service;

import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.collector.model.enums.*;
import ru.yandex.practicum.kafka.telemetry.collector.model.hub.*;
import ru.yandex.practicum.kafka.telemetry.collector.model.hub.HubEvent;

import java.time.Instant;
import java.util.stream.Collectors;

public class HubEventGrpcMapper {

    public static HubEvent toModel(HubEventProto proto) {

        return switch (proto.getPayloadCase()) {

            case DEVICE_ADDED     -> mapDeviceAdded(proto);
            case DEVICE_REMOVED   -> mapDeviceRemoved(proto);
            case SCENARIO_ADDED   -> mapScenarioAdded(proto);
            case SCENARIO_REMOVED -> mapScenarioRemoved(proto);

            default -> throw new IllegalArgumentException(
                    "Unknown hub event: " + proto.getPayloadCase());
        };
    }

    private static DeviceAddedEvent mapDeviceAdded(HubEventProto proto) {
        DeviceAddedEvent event = new DeviceAddedEvent();
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto));

        DeviceAddedEventProto payload = proto.getDeviceAdded();
        event.setId(payload.getId());
        event.setDeviceType(mapDeviceType(payload.getType()));

        return event;
    }

    private static DeviceRemovedEvent mapDeviceRemoved(HubEventProto proto) {
        DeviceRemovedEvent event = new DeviceRemovedEvent();
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto));

        DeviceRemovedEventProto payload = proto.getDeviceRemoved();
        event.setId(payload.getId());

        return event;
    }

    private static ScenarioAddedEvent mapScenarioAdded(HubEventProto proto) {
        ScenarioAddedEvent event = new ScenarioAddedEvent();
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto));

        ScenarioAddedEventProto payload = proto.getScenarioAdded();

        event.setName(payload.getName());

        event.setConditions(
                payload.getConditionList().stream()
                        .map(HubEventGrpcMapper::mapCondition)
                        .collect(Collectors.toList())
        );

        event.setActions(
                payload.getActionList().stream()
                        .map(HubEventGrpcMapper::mapAction)
                        .collect(Collectors.toList())
        );

        return event;
    }

    private static ScenarioRemovedEvent mapScenarioRemoved(HubEventProto proto) {
        ScenarioRemovedEvent event = new ScenarioRemovedEvent();
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto));

        ScenarioRemovedEventProto payload = proto.getScenarioRemoved();
        event.setName(payload.getName());

        return event;
    }

    private static ScenarioCondition mapCondition(ScenarioConditionProto proto) {
        ScenarioCondition condition = new ScenarioCondition();

        condition.setSensorId(proto.getSensorId());
        condition.setType(mapConditionType(proto.getType()));
        condition.setOperation(mapOperation(proto.getOperation()));

        switch (proto.getValueCase()) {
            case BOOL_VALUE -> condition.setValue(proto.getBoolValue() ? 1 : 0);
            case INT_VALUE  -> condition.setValue(proto.getIntValue());
            case VALUE_NOT_SET -> condition.setValue(null);
        }

        return condition;
    }

    private static DeviceAction mapAction(DeviceActionProto proto) {
        DeviceAction action = new DeviceAction();
        action.setSensorId(proto.getSensorId());
        action.setType(mapActionType(proto.getType()));

        if (proto.hasValue()) {
            action.setValue(proto.getValue());
        }

        return action;
    }

    private static DeviceType mapDeviceType(DeviceTypeProto proto) {
        return switch (proto) {
            case MOTION_SENSOR     -> DeviceType.MOTION_SENSOR;
            case TEMPERATURE_SENSOR-> DeviceType.TEMPERATURE_SENSOR;
            case LIGHT_SENSOR      -> DeviceType.LIGHT_SENSOR;
            case CLIMATE_SENSOR    -> DeviceType.CLIMATE_SENSOR;
            case SWITCH_SENSOR     -> DeviceType.SWITCH_SENSOR;
            case UNRECOGNIZED      -> throw new IllegalArgumentException("Unknown DeviceType");
        };
    }

    private static ConditionType mapConditionType(ConditionTypeProto proto) {
        return switch (proto) {
            case MOTION      -> ConditionType.MOTION;
            case LUMINOSITY  -> ConditionType.LUMINOSITY;
            case SWITCH      -> ConditionType.SWITCH;
            case TEMPERATURE -> ConditionType.TEMPERATURE;
            case CO2LEVEL    -> ConditionType.CO2LEVEL;
            case HUMIDITY    -> ConditionType.HUMIDITY;
            case UNRECOGNIZED-> throw new IllegalArgumentException("Unknown ConditionType");
        };
    }

    private static ConditionOperation mapOperation(ConditionOperationProto proto) {
        return switch (proto) {
            case EQUALS      -> ConditionOperation.EQUALS;
            case GREATER_THAN-> ConditionOperation.GREATER_THAN;
            case LOWER_THAN  -> ConditionOperation.LOWER_THAN;
            case UNRECOGNIZED-> throw new IllegalArgumentException("Unknown ConditionOperation");
        };
    }

    private static ActionType mapActionType(ActionTypeProto proto) {
        return switch (proto) {
            case ACTIVATE   -> ActionType.ACTIVATE;
            case DEACTIVATE -> ActionType.DEACTIVATE;
            case INVERSE    -> ActionType.INVERSE;
            case SET_VALUE  -> ActionType.SET_VALUE;
            case UNRECOGNIZED -> throw new IllegalArgumentException("Unknown ActionType");
        };
    }

    private static Instant toInstant(HubEventProto proto) {
        return Instant.ofEpochSecond(
                proto.getTimestamp().getSeconds(),
                proto.getTimestamp().getNanos());
    }
}

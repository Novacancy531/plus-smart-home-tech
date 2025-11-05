package ru.yandex.practicum.kafka.telemetry.collector.service;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.collector.model.hub.*;
import ru.yandex.practicum.kafka.telemetry.collector.model.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

public class AvroMapper {

    public static SpecificRecordBase toAvro(SensorEvent event) {
        return switch (event.getType()) {
            case LIGHT_SENSOR_EVENT -> {
                LightSensorEvent light = (LightSensorEvent) event;
                yield SensorEventAvro.newBuilder()
                        .setId(light.getId())
                        .setHubId(light.getHubId())
                        .setTimestamp(light.getTimestamp().toEpochMilli())
                        .setPayload(LightSensorAvro.newBuilder()
                                .setId(light.getId())
                                .setHubId(light.getHubId())
                                .setTimestamp(light.getTimestamp().toEpochMilli())
                                .setLinkQuality(light.getLinkQuality())
                                .setLuminosity(light.getLuminosity())
                                .build())
                        .build();
            }
            case TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorEvent temp = (TemperatureSensorEvent) event;
                yield SensorEventAvro.newBuilder()
                        .setId(temp.getId())
                        .setHubId(temp.getHubId())
                        .setTimestamp(temp.getTimestamp().toEpochMilli())
                        .setPayload(TemperatureSensorAvro.newBuilder()
                                .setId(temp.getId())
                                .setHubId(temp.getHubId())
                                .setTimestamp(temp.getTimestamp().toEpochMilli())
                                .setTemperatureC(temp.getTemperatureC())
                                .setTemperatureF(temp.getTemperatureF())
                                .build())
                        .build();
            }
            case MOTION_SENSOR_EVENT -> {
                MotionSensorEvent motion = (MotionSensorEvent) event;
                yield SensorEventAvro.newBuilder()
                        .setId(motion.getId())
                        .setHubId(motion.getHubId())
                        .setTimestamp(motion.getTimestamp().toEpochMilli())
                        .setPayload(MotionSensorAvro.newBuilder()
                                .setId(motion.getId())
                                .setHubId(motion.getHubId())
                                .setTimestamp(motion.getTimestamp().toEpochMilli())
                                .setLinkQuality(motion.getLinkQuality())
                                .setMotion(motion.isMotion())
                                .setVoltage(motion.getVoltage())
                                .build())
                        .build();
            }
            case CLIMATE_SENSOR_EVENT -> {
                ClimateSensorEvent climate = (ClimateSensorEvent) event;
                yield SensorEventAvro.newBuilder()
                        .setId(climate.getId())
                        .setHubId(climate.getHubId())
                        .setTimestamp(climate.getTimestamp().toEpochMilli())
                        .setPayload(ClimateSensorAvro.newBuilder()
                                .setId(climate.getId())
                                .setHubId(climate.getHubId())
                                .setTimestamp(climate.getTimestamp().toEpochMilli())
                                .setTemperatureC(climate.getTemperatureC())
                                .setHumidity(climate.getHumidity())
                                .setCo2Level(climate.getCo2Level())
                                .build())
                        .build();
            }
            case SWITCH_SENSOR_EVENT -> {
                SwitchSensorEvent sw = (SwitchSensorEvent) event;
                yield SensorEventAvro.newBuilder()
                        .setId(sw.getId())
                        .setHubId(sw.getHubId())
                        .setTimestamp(sw.getTimestamp().toEpochMilli())
                        .setPayload(SwitchSensorAvro.newBuilder()
                                .setId(sw.getId())
                                .setHubId(sw.getHubId())
                                .setTimestamp(sw.getTimestamp().toEpochMilli())
                                .setState(sw.isState())
                                .build())
                        .build();
            }
        };
    }

    public static SpecificRecordBase toAvro(HubEvent event) {
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
            default -> throw new IllegalArgumentException("Unsupported type: " + event.getType());
        }

        return builder.build();
    }
}

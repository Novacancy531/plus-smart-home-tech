package ru.yandex.practicum.kafka.telemetry.collector.service;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.collector.model.sensor.*;

import java.time.Instant;

public class SensorEventGrpcMapper {

    public static SensorEvent toModel(SensorEventProto proto) {
        return switch (proto.getPayloadCase()) {

            case MOTION_SENSOR_EVENT -> mapMotion(proto);
            case TEMPERATURE_SENSOR_EVENT -> mapTemperature(proto);
            case LIGHT_SENSOR_EVENT -> mapLight(proto);
            case CLIMATE_SENSOR_EVENT -> mapClimate(proto);
            case SWITCH_SENSOR_EVENT -> mapSwitch(proto);

            default -> throw new IllegalArgumentException(
                    "Unknown type: " + proto.getPayloadCase()
            );
        };
    }

    private static MotionSensorEvent mapMotion(SensorEventProto proto) {
        MotionSensorEvent event = new MotionSensorEvent();
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto));
        event.setLinkQuality(proto.getMotionSensorEvent().getLinkQuality());
        event.setVoltage(proto.getMotionSensorEvent().getVoltage());
        event.setMotion(proto.getMotionSensorEvent().getMotion());
        return event;
    }

    private static TemperatureSensorEvent mapTemperature(SensorEventProto proto) {
        TemperatureSensorEvent event = new TemperatureSensorEvent();
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto));
        event.setTemperatureC(proto.getTemperatureSensorEvent().getTemperatureC());
        event.setTemperatureF(proto.getTemperatureSensorEvent().getTemperatureF());
        return event;
    }

    private static LightSensorEvent mapLight(SensorEventProto proto) {
        LightSensorEvent event = new LightSensorEvent();
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto));
        event.setLuminosity(proto.getLightSensorEvent().getLuminosity());
        return event;
    }

    private static ClimateSensorEvent mapClimate(SensorEventProto proto) {
        ClimateSensorEvent event = new ClimateSensorEvent();
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto));
        event.setTemperatureC(proto.getClimateSensorEvent().getTemperatureC());
        event.setHumidity(proto.getClimateSensorEvent().getHumidity());
        event.setCo2Level(proto.getClimateSensorEvent().getCo2Level());
        return event;
    }

    private static SwitchSensorEvent mapSwitch(SensorEventProto proto) {
        SwitchSensorEvent event = new SwitchSensorEvent();
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto));
        event.setState(proto.getSwitchSensorEvent().getState());
        return event;
    }

    private static Instant toInstant(SensorEventProto proto) {
        return Instant.ofEpochSecond(
                proto.getTimestamp().getSeconds(),
                proto.getTimestamp().getNanos()
        );
    }
}

package ru.yandex.practicum.kafka.telemetry.collector.service;

import ru.yandex.practicum.kafka.telemetry.collector.model.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class SensorAvroMapper {

    public static SensorEventAvro sensorToAvro(SensorEvent event) {
        SensorEventAvro.Builder builder = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        switch (event.getType()) {
            case CLIMATE_SENSOR_EVENT -> {
                ClimateSensorEvent e = (ClimateSensorEvent) event;
                builder.setPayload(ClimateSensorAvro.newBuilder()
                        .setId(e.getId())
                        .setHubId(e.getHubId())
                        .setTimestamp(e.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .setTemperatureC(e.getTemperatureC())
                        .setHumidity(e.getHumidity())
                        .setCo2Level(e.getCo2Level())
                        .build());
            }
            case LIGHT_SENSOR_EVENT -> {
                LightSensorEvent e = (LightSensorEvent) event;
                builder.setPayload(LightSensorAvro.newBuilder()
                        .setId(e.getId())
                        .setHubId(e.getHubId())
                        .setTimestamp(e.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .setLinkQuality(e.getLinkQuality())
                        .setLuminosity(e.getLuminosity())
                        .build());
            }
            case MOTION_SENSOR_EVENT -> {
                MotionSensorEvent e = (MotionSensorEvent) event;
                builder.setPayload(MotionSensorAvro.newBuilder()
                        .setId(e.getId())
                        .setHubId(e.getHubId())
                        .setTimestamp(e.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .setLinkQuality(e.getLinkQuality())
                        .setMotion(e.isMotion())
                        .setVoltage(e.getVoltage())
                        .build());
            }
            case SWITCH_SENSOR_EVENT -> {
                SwitchSensorEvent e = (SwitchSensorEvent) event;
                builder.setPayload(SwitchSensorAvro.newBuilder()
                        .setId(e.getId())
                        .setHubId(e.getHubId())
                        .setTimestamp(e.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .setState(e.isState())
                        .build());
            }
            case TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorEvent e = (TemperatureSensorEvent) event;
                builder.setPayload(TemperatureSensorAvro.newBuilder()
                        .setId(e.getId())
                        .setHubId(e.getHubId())
                        .setTimestamp(e.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .setTemperatureC(e.getTemperatureC())
                        .setTemperatureF(e.getTemperatureF())
                        .build());
            }
            default -> throw new IllegalArgumentException("Unsupported SensorEvent type: " + event.getType());
        }

        return builder.build();
    }
}

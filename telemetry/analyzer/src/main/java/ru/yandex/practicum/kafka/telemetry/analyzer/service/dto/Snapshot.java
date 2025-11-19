package ru.yandex.practicum.kafka.telemetry.analyzer.service.dto;

import java.util.List;

public class Snapshot {

    private String hubId;
    private List<SensorState> sensors;

    public String getHubId() {
        return hubId;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public List<SensorState> getSensors() {
        return sensors;
    }

    public void setSensors(List<SensorState> sensors) {
        this.sensors = sensors;
    }
}

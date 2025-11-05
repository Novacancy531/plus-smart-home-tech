package ru.yandex.practicum.kafka.telemetry.collector.model.sensor;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.collector.model.enums.SensorEventType;

@Getter
@Setter
public class LightSensorEvent extends SensorEvent {

    private int linkQuality;
    private int luminosity;

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}

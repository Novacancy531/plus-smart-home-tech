package ru.yandex.practicum.kafka.telemetry.collector.model.sensor;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.collector.model.enums.SensorEventType;

@Getter
@Setter
public class SwitchSensorEvent extends SensorEvent {

    private boolean state;

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}

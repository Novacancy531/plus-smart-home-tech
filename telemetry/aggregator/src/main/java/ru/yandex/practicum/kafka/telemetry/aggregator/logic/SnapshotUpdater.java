package ru.yandex.practicum.kafka.telemetry.aggregator.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class SnapshotUpdater {

    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();


    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {

        String hubId = event.getHubId();
        String sensorId = event.getId();

        Instant eventInstant = Instant.ofEpochMilli(event.getTimestamp());

        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(
                hubId,
                id -> SensorsSnapshotAvro.newBuilder()
                        .setHubId(id)
                        .setTimestamp(eventInstant)
                        .setSensorsState(new HashMap<>())
                        .build()
        );

        SensorStateAvro oldState = snapshot.getSensorsState().get(sensorId);

        if (oldState != null) {

            if (oldState.getTimestamp().isAfter(eventInstant)) {
                return Optional.empty();
            }

            if (oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(eventInstant)
                .setData(event.getPayload())
                .build();

        snapshot.getSensorsState().put(sensorId, newState);

        snapshot.setTimestamp(eventInstant);

        return Optional.of(snapshot);
    }

}

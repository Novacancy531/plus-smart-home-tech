package ru.yandex.practicum.kafka.telemetry.aggregator.state;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SnapshotsStorage {

    private final Map<String, SensorsSnapshotAvro> snapshots = new ConcurrentHashMap<>();

    public SensorsSnapshotAvro get(String hubId) {
        return snapshots.get(hubId);
    }

    public void put(String hubId, SensorsSnapshotAvro snapshot) {
        snapshots.put(hubId, snapshot);
    }
}

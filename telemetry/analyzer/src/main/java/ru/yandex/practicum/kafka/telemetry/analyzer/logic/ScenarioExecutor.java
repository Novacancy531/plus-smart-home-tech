package ru.yandex.practicum.kafka.telemetry.analyzer.logic;

import com.google.protobuf.util.Timestamps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;

import ru.yandex.practicum.kafka.telemetry.analyzer.domain.*;
import ru.yandex.practicum.kafka.telemetry.analyzer.grpc.HubRouterClient;

import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ScenarioExecutor {

    private final HubRouterClient hubRouterClient;

    public void processSnapshot(SensorsSnapshotAvro snapshot, List<Scenario> scenarios) {
        String hubId = snapshot.getHubId().toString();

        Map<String, SensorStateAvro> states = new HashMap<>();
        snapshot.getSensorsState().forEach((k, v) -> states.put(k.toString(), v));

        for (Scenario scenario : scenarios) {

            boolean allConditionsOk = scenario.getScenarioConditions().stream()
                    .allMatch(sc -> checkCondition(sc, states));

            if (!allConditionsOk) {
                continue;
            }

            for (ScenarioAction action : scenario.getScenarioActions()) {
                sendAction(hubId, scenario.getName(), action);
            }
        }
    }

    private boolean checkCondition(ScenarioCondition sc, Map<String, SensorStateAvro> states) {
        Sensor sensor = sc.getSensor();
        SensorStateAvro state = states.get(sensor.getId());

        if (state == null) {
            return false;
        }

        Condition condition = sc.getCondition();

        Integer actual = extractIntValue(state, condition.getType());
        Integer expected = condition.getValue();

        if (actual == null || expected == null) {
            return false;
        }

        return switch (condition.getOperation()) {
            case "GREATER_THAN" -> actual > expected;
            case "LOWER_THAN" -> actual < expected;
            case "EQUALS" -> Objects.equals(actual, expected);
            default -> false;
        };
    }

    private Integer extractIntValue(SensorStateAvro state, String type) {
        Object data = state.getData();

        if (data == null || type == null) return null;

        return switch (type) {
            case "TEMPERATURE" -> {
                if (data instanceof TemperatureSensorAvro t) yield t.getTemperatureC();
                if (data instanceof ClimateSensorAvro c) yield c.getTemperatureC();
                yield null;
            }
            case "CO2LEVEL" -> {
                if (data instanceof ClimateSensorAvro c) yield c.getCo2Level();
                yield null;
            }
            case "HUMIDITY" -> {
                if (data instanceof ClimateSensorAvro c) yield c.getHumidity();
                yield null;
            }
            case "LUMINOSITY" -> {
                if (data instanceof LightSensorAvro l) yield l.getLuminosity();
                yield null;
            }
            case "SWITCH" -> {
                if (data instanceof SwitchSensorAvro s) yield s.getState() ? 1 : 0;
                yield null;
            }
            case "MOTION" -> {
                if (data instanceof MotionSensorAvro m) yield m.getMotion() ? 1 : 0;
                yield null;
            }
            default -> null;
        };
    }

    private void sendAction(String hubId, String scenarioName, ScenarioAction sa) {
        Action action = sa.getAction();

        ActionTypeProto typeProto = ActionTypeProto.valueOf(action.getType());

        DeviceActionProto.Builder ab = DeviceActionProto.newBuilder()
                .setSensorId(sa.getSensor().getId())
                .setType(typeProto);

        if (action.getValue() != null) {
            ab.setValue(action.getValue());
        }

        DeviceActionRequest req = DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(ab.build())
                .setTimestamp(Timestamps.fromMillis(System.currentTimeMillis()))
                .build();

        hubRouterClient.sendDeviceAction(req);
    }
}

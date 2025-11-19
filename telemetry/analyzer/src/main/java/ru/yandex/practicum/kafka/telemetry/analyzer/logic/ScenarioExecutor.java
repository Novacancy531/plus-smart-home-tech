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

import java.util.*;

@Service
@RequiredArgsConstructor
public class ScenarioExecutor {

    private final HubRouterClient hubRouterClient;

    public void processSnapshot(SensorsSnapshotAvro snapshot, List<Scenario> scenarios) {
        String hubId = snapshot.getHubId().toString();

        Map<String, SensorStateAvro> states = new HashMap<>();
        snapshot.getSensorsState().forEach((k, v) -> states.put(k.toString(), v));

        for (Scenario scenario : scenarios) {

            boolean ok = scenario.getConditions().stream()
                    .allMatch(cond -> checkCondition(cond, states));

            if (!ok) {
                continue;
            }

            for (ScenarioAction action : scenario.getActions()) {
                sendAction(hubId, scenario.getName(), action);
            }
        }
    }

    private boolean checkCondition(ScenarioCondition sc, Map<String, SensorStateAvro> states) {
        Sensor sensor = sc.getSensor();
        if (sensor == null) {
            return false;
        }

        SensorStateAvro state = states.get(sensor.getId());
        if (state == null) {
            return false;
        }

        Condition condition = sc.getCondition();
        if (condition == null || condition.getType() == null) {
            return false;
        }

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
        if (data == null) {
            return null;
        }

        String t = type.toUpperCase(Locale.ROOT);

        switch (t) {
            case "TEMPERATURE":
            case "CLIMATE_TEMPERATURE":
                if (data instanceof TemperatureSensorAvro t1) return t1.getTemperatureC();
                if (data instanceof ClimateSensorAvro c) return c.getTemperatureC();
                return null;

            case "CO2LEVEL":
                if (data instanceof ClimateSensorAvro c2) return c2.getCo2Level();
                return null;

            case "HUMIDITY":
                if (data instanceof ClimateSensorAvro c3) return c3.getHumidity();
                return null;

            case "LUMINOSITY":
                if (data instanceof LightSensorAvro l) return l.getLuminosity();
                return null;

            case "MOTION":
                if (data instanceof MotionSensorAvro m) return m.getMotion() ? 1 : 0;
                return null;

            case "SWITCH":
                if (data instanceof SwitchSensorAvro s) return s.getState() ? 1 : 0;
                return null;

            default:
                return null;
        }
    }

    private void sendAction(String hubId, String scenarioName, ScenarioAction scenarioAction) {
        Action action = scenarioAction.getAction();

        DeviceActionProto.Builder builder = DeviceActionProto.newBuilder()
                .setSensorId(scenarioAction.getSensor().getId())
                .setType(ActionTypeProto.valueOf(action.getType()));

        if (action.getValue() != null) {
            builder.setValue(action.getValue());
        }

        DeviceActionRequest request = DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(builder.build())
                .setTimestamp(Timestamps.fromMillis(System.currentTimeMillis()))
                .build();

        hubRouterClient.sendDeviceAction(request);
    }
}

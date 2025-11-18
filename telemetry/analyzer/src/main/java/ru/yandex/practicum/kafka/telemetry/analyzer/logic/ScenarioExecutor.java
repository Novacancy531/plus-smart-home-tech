package ru.yandex.practicum.kafka.telemetry.analyzer.logic;

import com.google.protobuf.util.Timestamps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.analyzer.domain.Action;
import ru.yandex.practicum.kafka.telemetry.analyzer.domain.Condition;
import ru.yandex.practicum.kafka.telemetry.analyzer.domain.Scenario;
import ru.yandex.practicum.kafka.telemetry.analyzer.domain.ScenarioAction;
import ru.yandex.practicum.kafka.telemetry.analyzer.domain.ScenarioCondition;
import ru.yandex.practicum.kafka.telemetry.analyzer.domain.Sensor;
import ru.yandex.practicum.kafka.telemetry.analyzer.grpc.HubRouterClient;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

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
        Map<CharSequence, SensorStateAvro> states = new HashMap<>();
        snapshot.getSensorsState().forEach((k, v) -> states.put(k, v));


        for (Scenario scenario : scenarios) {
            boolean allConditionsOk = scenario.getConditions().stream()
                    .allMatch(sc -> checkCondition(sc, states));

            if (!allConditionsOk) {
                continue;
            }

            for (ScenarioAction action : scenario.getActions()) {
                sendAction(hubId, scenario.getName(), action);
            }
        }
    }

    private boolean checkCondition(ScenarioCondition scenarioCondition,
                                   Map<CharSequence, SensorStateAvro> states) {
        Sensor sensor = scenarioCondition.getSensor();
        SensorStateAvro state = states.get(sensor.getId());
        if (state == null) {
            return false;
        }

        Condition condition = scenarioCondition.getCondition();
        Integer actual = extractIntValue(state, condition.getType());
        Integer expected = condition.getValue();
        if (actual == null || expected == null) {
            return false;
        }

        String op = condition.getOperation();
        return switch (op) {
            case "GREATER_THAN" -> actual > expected;
            case "LOWER_THAN" -> actual < expected;
            case "EQUALS" -> Objects.equals(actual, expected);
            default -> false;
        };
    }

    private Integer extractIntValue(SensorStateAvro state, String type) {
        Object data = state.getData();
        if (data == null || type == null) {
            return null;
        }

        if ("TEMPERATURE".equals(type)) {
            if (data instanceof TemperatureSensorAvro t) {
                return t.getTemperatureC();
            }
            if (data instanceof ClimateSensorAvro c) {
                return c.getTemperatureC();
            }
            return null;
        }

        if ("CO2LEVEL".equals(type)) {
            if (data instanceof ClimateSensorAvro c) {
                return c.getCo2Level();
            }
            return null;
        }

        if ("HUMIDITY".equals(type)) {
            if (data instanceof ClimateSensorAvro c) {
                return c.getHumidity();
            }
            return null;
        }

        if ("LUMINOSITY".equals(type)) {
            if (data instanceof LightSensorAvro l) {
                return l.getLuminosity();
            }
            return null;
        }

        if ("SWITCH".equals(type)) {
            if (data instanceof SwitchSensorAvro s) {
                return s.getState() ? 1 : 0;
            }
            return null;
        }

        if ("MOTION".equals(type)) {
            if (data instanceof MotionSensorAvro m) {
                return m.getMotion() ? 1 : 0;
            }
            return null;
        }

        return null;
    }

    private void sendAction(String hubId, String scenarioName, ScenarioAction scenarioAction) {
        Action action = scenarioAction.getAction();

        ActionTypeProto typeProto = ActionTypeProto.valueOf(action.getType());

        DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder()
                .setSensorId(scenarioAction.getSensor().getId())
                .setType(typeProto);

        if (action.getValue() != null) {
            actionBuilder.setValue(action.getValue());
        }

        DeviceActionRequest request = DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(actionBuilder.build())
                .setTimestamp(Timestamps.fromMillis(System.currentTimeMillis()))
                .build();

        hubRouterClient.sendDeviceAction(request);
    }
}

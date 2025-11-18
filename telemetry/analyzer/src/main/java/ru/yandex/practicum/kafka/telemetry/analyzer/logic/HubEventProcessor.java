package ru.yandex.practicum.kafka.telemetry.analyzer.logic;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.analyzer.domain.*;
import ru.yandex.practicum.kafka.telemetry.analyzer.repository.ActionRepository;
import ru.yandex.practicum.kafka.telemetry.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.kafka.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final KafkaConsumer<String, HubEventAvro> hubEventsConsumer;
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Value("${analyzer.topics.hub-events}")
    private String hubEventsTopic;

    private volatile boolean running = true;

    public void shutdown() {
        running = false;
        hubEventsConsumer.wakeup();
    }

    @Override
    public void run() {
        hubEventsConsumer.subscribe(List.of(hubEventsTopic));

        try {
            while (running) {
                ConsumerRecords<String, HubEventAvro> records =
                        hubEventsConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    if (event != null) {
                        processEvent(event);
                    }
                }
            }
        } catch (WakeupException e) {
            if (running) {
                throw e;
            }
        } finally {
            hubEventsConsumer.close();
        }
    }

    private void processEvent(HubEventAvro event) {
        String hubId = event.getHubId().toString();
        Object payload = event.getPayload();

        if (payload instanceof DeviceAddedEventAvro added) {
            handleDeviceAdded(hubId, added);
        } else if (payload instanceof DeviceRemovedEventAvro removed) {
            handleDeviceRemoved(hubId, removed);
        } else if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
            handleScenarioAdded(hubId, scenarioAdded);
        } else if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
            handleScenarioRemoved(hubId, scenarioRemoved);
        }
    }

    private void handleDeviceAdded(String hubId, DeviceAddedEventAvro added) {
        String sensorId = added.getId().toString();

        Sensor sensor = sensorRepository.findById(sensorId).orElse(null);
        if (sensor == null) {
            sensor = Sensor.builder()
                    .id(sensorId)
                    .hubId(hubId)
                    .build();
        } else if (!Objects.equals(sensor.getHubId(), hubId)) {
            sensor.setHubId(hubId);
        }

        sensorRepository.save(sensor);
    }

    private void handleDeviceRemoved(String hubId, DeviceRemovedEventAvro removed) {
        String sensorId = removed.getId().toString();

        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        for (Scenario scenario : scenarios) {
            if (scenario.getScenarioConditions() != null) {
                scenario.getScenarioConditions().removeIf(
                        sc -> sensorId.equals(sc.getSensor().getId())
                );
            }
            if (scenario.getScenarioActions() != null) {
                scenario.getScenarioActions().removeIf(
                        sa -> sensorId.equals(sa.getSensor().getId())
                );
            }
            scenarioRepository.save(scenario);
        }

        sensorRepository.deleteById(sensorId);
    }

    private void handleScenarioRemoved(String hubId, ScenarioRemovedEventAvro removed) {
        String name = removed.getName().toString();
        scenarioRepository.findByHubIdAndName(hubId, name)
                .ifPresent(scenarioRepository::delete);
    }

    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro added) {
        String name = added.getName().toString();

        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, name)
                .orElseGet(() -> Scenario.builder()
                        .hubId(hubId)
                        .name(name)
                        .build()
                );

        if (scenario.getId() == null) {
            scenario = scenarioRepository.save(scenario);
        }

        List<ScenarioCondition> scenarioConditions = new ArrayList<>();
        List<ScenarioAction> scenarioActions = new ArrayList<>();

        for (ScenarioConditionAvro condAvro : added.getConditions()) {
            String sensorId = condAvro.getSensorId().toString();
            Sensor sensor = ensureSensor(hubId, sensorId);

            Condition condition = Condition.builder()
                    .type(condAvro.getType().name())
                    .operation(condAvro.getOperation().name())
                    .value(extractConditionValue(condAvro))
                    .build();
            condition = conditionRepository.save(condition);

            ScenarioCondition sc = ScenarioCondition.builder()
                    .id(new ScenarioConditionId(
                            scenario.getId(),
                            sensorId,
                            condition.getId()
                    ))
                    .scenario(scenario)
                    .sensor(sensor)
                    .condition(condition)
                    .build();

            scenarioConditions.add(sc);
        }

        for (DeviceActionAvro actionAvro : added.getActions()) {
            String sensorId = actionAvro.getSensorId().toString();
            Sensor sensor = ensureSensor(hubId, sensorId);

            Action action = Action.builder()
                    .type(actionAvro.getType().name())
                    .value(extractActionValue(actionAvro))
                    .build();
            action = actionRepository.save(action);

            ScenarioAction sa = ScenarioAction.builder()
                    .id(new ScenarioActionId(
                            scenario.getId(),
                            sensorId,
                            action.getId()
                    ))
                    .scenario(scenario)
                    .sensor(sensor)
                    .action(action)
                    .build();

            scenarioActions.add(sa);
        }

        scenario.setScenarioConditions(scenarioConditions);
        scenario.setScenarioActions(scenarioActions);

        scenarioRepository.save(scenario);
    }

    private Sensor ensureSensor(String hubId, String sensorId) {
        return sensorRepository.findById(sensorId)
                .orElseGet(() -> sensorRepository.save(
                        Sensor.builder()
                                .id(sensorId)
                                .hubId(hubId)
                                .build()
                ));
    }

    private Integer extractConditionValue(ScenarioConditionAvro condAvro) {
        Object v = condAvro.getValue();
        if (v instanceof Integer i) return i;
        if (v instanceof Boolean b) return b ? 1 : 0;
        return null;
    }

    private Integer extractActionValue(DeviceActionAvro actionAvro) {
        Object v = actionAvro.getValue();
        if (v instanceof Integer i) return i;
        return null;
    }
}

package ru.yandex.practicum.kafka.telemetry.analyzer.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.analyzer.grpc.HubRouterClient;
import ru.yandex.practicum.kafka.telemetry.analyzer.model.Action;
import ru.yandex.practicum.kafka.telemetry.analyzer.model.Condition;
import ru.yandex.practicum.kafka.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.kafka.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.analyzer.service.dto.Snapshot;
import ru.yandex.practicum.kafka.telemetry.analyzer.service.dto.SensorState;

import java.time.Duration;
import java.util.*;

@Component
public class SnapshotProcessor {

    private final ScenarioRepository scenarioRepository;
    private final HubRouterClient hubRouterClient;
    private final ObjectMapper objectMapper;
    private final String bootstrapServers;
    private final String topic;
    private final String groupId;

    public SnapshotProcessor(
            ScenarioRepository scenarioRepository,
            HubRouterClient hubRouterClient,
            ObjectMapper objectMapper,
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${telemetry.topics.snapshots}") String topic,
            @Value("${telemetry.consumers.snapshots-group-id}") String groupId) {
        this.scenarioRepository = scenarioRepository;
        this.hubRouterClient = hubRouterClient;
        this.objectMapper = objectMapper;
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.groupId = groupId;
    }

    public void start() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(List.of(topic));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
            for (ConsumerRecord<String, String> record : records) {
                handleRecord(record.value());
            }
        }
    }

    private void handleRecord(String value) {
        try {
            Snapshot snapshot = objectMapper.readValue(value, Snapshot.class);
            List<Scenario> scenarios = scenarioRepository.findByHubId(snapshot.getHubId());

            for (Scenario scenario : scenarios) {
                List<String> matchedSensors = matchScenario(snapshot, scenario);

                if (!matchedSensors.isEmpty()) {
                    for (Action action : scenario.getActions()) {
                        for (String sensorId : matchedSensors) {
                            hubRouterClient.sendAction(
                                    snapshot.getHubId(),
                                    scenario.getName(),
                                    sensorId,
                                    action
                            );
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private List<String> matchScenario(Snapshot snapshot, Scenario scenario) {
        List<SensorState> sensors = snapshot.getSensors();
        List<Condition> conditions = scenario.getConditions();
        List<String> matchedSensors = new ArrayList<>();

        for (Condition condition : conditions) {
            boolean matched = false;

            for (SensorState state : sensors) {
                if (!condition.getType().equals(state.getType())) continue;
                if (state.getValue() == null) continue;

                Integer value = state.getValue();
                Integer t = condition.getValue();
                String op = condition.getOperation();

                if (op.equals(">") && value > t) matched = true;
                else if (op.equals("<") && value < t) matched = true;
                else if (op.equals("==") && value.equals(t)) matched = true;

                if (matched) {
                    matchedSensors.add(state.getId());
                    break;
                }
            }

            if (!matched) return List.of();
        }

        return matchedSensors;
    }
}

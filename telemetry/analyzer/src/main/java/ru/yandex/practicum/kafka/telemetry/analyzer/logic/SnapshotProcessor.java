package ru.yandex.practicum.kafka.telemetry.analyzer.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.analyzer.domain.Scenario;
import ru.yandex.practicum.kafka.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;
    private final ScenarioRepository scenarioRepository;
    private final ScenarioExecutor scenarioExecutor;

    public void start() {
        try {
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records =
                        consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshot = record.value();
                    if (snapshot == null) {
                        continue;
                    }
                    String hubId = snapshot.getHubId();
                    List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
                    if (!scenarios.isEmpty()) {
                        scenarioExecutor.processSnapshot(snapshot, scenarios);
                    }
                }
            }
        } catch (WakeupException e) {
            log.info("SnapshotProcessor wakeup");
        } catch (Exception e) {
            log.error("SnapshotProcessor error", e);
        } finally {
            consumer.close();
        }
    }
}

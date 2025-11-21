package ru.yandex.practicum.kafka.telemetry.aggregator.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final KafkaProducer<String, SensorsSnapshotAvro> producer;
    private final SnapshotUpdater snapshotUpdater;

    @Value("${kafka.topics.sensors}")
    private String INPUT_TOPIC;
    @Value("${kafka.topics.snapshots}")
    private String OUTPUT_TOPIC;


    public void start() {

        log.info("Aggregator starting…");
        consumer.subscribe(List.of(INPUT_TOPIC));

        try {

            while (true) {

                ConsumerRecords<String, SensorEventAvro> records =
                        consumer.poll(Duration.ofMillis(500));

                for (ConsumerRecord<String, SensorEventAvro> rec : records) {

                    SensorEventAvro event = rec.value();
                    if (event == null) {
                        continue;
                    }

                    var updated = snapshotUpdater.updateState(event);

                    if (updated.isPresent()) {

                        SensorsSnapshotAvro snapshot = updated.get();

                        log.debug("Snapshot updated for hub={}, sending",
                                snapshot.getHubId());

                        producer.send(new ProducerRecord<>(
                                OUTPUT_TOPIC,
                                snapshot.getHubId(),
                                snapshot
                        ));
                    }
                }

                consumer.commitAsync();
            }

        } catch (WakeupException ignore) {
        } catch (Exception e) {
            log.error("Ошибка в цикле агрегации", e);
        } finally {

            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                log.info("Закрываем consumer");
                consumer.close();

                log.info("Закрываем producer");
                producer.close();
            }
        }
    }
}

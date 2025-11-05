package ru.yandex.practicum.kafka.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.collector.kafka.AvroSerializer;
import ru.yandex.practicum.kafka.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.kafka.telemetry.collector.model.sensor.SensorEvent;

import static ru.yandex.practicum.kafka.telemetry.collector.service.AvroMapper.toAvro;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectorService {

    private final KafkaProducer<String, byte[]> producer;

    public static final String SENSOR_TOPIC = "telemetry.sensors.v1";
    public static final String HUB_TOPIC = "telemetry.hubs.v1";

    public void sendSensorEvent(SensorEvent event) {
        SpecificRecordBase avroRecord = toAvro(event);
        byte[] data = AvroSerializer.serialize(avroRecord);

        log.info("Sending sensor event {}", data);
        producer.send(new ProducerRecord<>(SENSOR_TOPIC, event.getId(), data));
    }

    public void sendHubEvent(HubEvent event) {
        SpecificRecordBase avroRecord = toAvro(event);
        byte[] data = AvroSerializer.serialize(avroRecord);

        log.info("Sending hub event {}", data);
        producer.send(new ProducerRecord<>(HUB_TOPIC, event.getHubId(), data));
    }
}

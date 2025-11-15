package ru.yandex.practicum.kafka.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.collector.kafka.AvroSerializer;
import ru.yandex.practicum.kafka.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.kafka.telemetry.collector.model.sensor.SensorEvent;

import static ru.yandex.practicum.kafka.telemetry.collector.service.HubAvroMapper.hubToAvro;
import static ru.yandex.practicum.kafka.telemetry.collector.service.SensorAvroMapper.sensorToAvro;

@Service
@RequiredArgsConstructor
public class CollectorService {

    private final KafkaProducer<String, byte[]> producer;

    @Value("${collector.kafka.topics.sensors}")
    private String sensorTopic;

    @Value("${collector.kafka.topics.hubs}")
    private String hubTopic;

    public void sendSensorEvent(SensorEvent event) {
        SpecificRecordBase avroRecord = sensorToAvro(event);
        byte[] data = AvroSerializer.serialize(avroRecord);
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                sensorTopic,
                null,
                event.getTimestamp().toEpochMilli(),
                event.getHubId(),
                data
        );
        producer.send(record);
    }

    public void sendHubEvent(HubEvent event) {
        SpecificRecordBase avroRecord = hubToAvro(event);
        byte[] data = AvroSerializer.serialize(avroRecord);
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                hubTopic,
                null,
                event.getTimestamp().toEpochMilli(),
                event.getHubId(),
                data
        );
        producer.send(record);
    }
}

package ru.yandex.practicum.kafka.telemetry.analyzer.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.analyzer.repository.SensorRepository;

import java.time.Duration;
import java.util.Properties;

@Component
public class HubEventProcessor implements Runnable {

    private final SensorRepository sensorRepository;
    private final ObjectMapper objectMapper;
    private final String bootstrapServers;
    private final String topic;
    private final String groupId;

    public HubEventProcessor(SensorRepository sensorRepository,
                             ObjectMapper objectMapper,
                             @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
                             @Value("${telemetry.topics.hub-events}") String topic,
                             @Value("${telemetry.consumers.hub-events-group-id}") String groupId) {
        this.sensorRepository = sensorRepository;
        this.objectMapper = objectMapper;
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.groupId = groupId;
    }

    @Override
    public void run() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(java.util.List.of(topic));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                String value = record.value();
            }
        }
    }
}

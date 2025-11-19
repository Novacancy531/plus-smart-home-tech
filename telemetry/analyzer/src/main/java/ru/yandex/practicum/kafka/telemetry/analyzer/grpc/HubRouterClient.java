package ru.yandex.practicum.kafka.telemetry.analyzer.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.time.Instant;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.analyzer.model.Action;

@Component
public class HubRouterClient {

    private final ManagedChannel channel;

    public HubRouterClient() {
        this.channel = ManagedChannelBuilder
                .forAddress("localhost", 59090)
                .usePlaintext()
                .build();
    }

    public void sendAction(String hubId, String scenarioName, Action action) {
        Instant now = Instant.now();
    }
}

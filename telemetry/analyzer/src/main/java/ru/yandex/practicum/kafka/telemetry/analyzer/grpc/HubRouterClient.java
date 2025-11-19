package ru.yandex.practicum.kafka.telemetry.analyzer.grpc;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;


@Service
@RequiredArgsConstructor
public class HubRouterClient {

    @GrpcClient("hub-router")
    private HubRouterControllerBlockingStub stub;

    @PostConstruct
    public void debug() {
        System.out.println("Hub router = " + stub);
    }

    public void sendDeviceAction(DeviceActionRequest request) {
        stub.handleDeviceAction(request);
    }
}

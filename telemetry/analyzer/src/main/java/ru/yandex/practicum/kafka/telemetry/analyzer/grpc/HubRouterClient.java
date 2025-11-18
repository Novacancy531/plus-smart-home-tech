package ru.yandex.practicum.kafka.telemetry.analyzer.grpc;

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

    public void sendDeviceAction(DeviceActionRequest request) {
        stub.handleDeviceAction(request);
    }
}

package ru.yandex.practicum.entity;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

@Value
@Builder
public class ErrorResponse {
    String errorCode;
    String message;
    Instant timestamp;
    Map<String, Object> details;
}

package ru.yandex.practicum.api.handler;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FeignProxyExceptionHandler {

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<String> handleFeign(feign.FeignException e) {
        return ResponseEntity
                .status(e.status())
                .contentType(MediaType.APPLICATION_JSON)
                .body(e.contentUTF8());
    }
}

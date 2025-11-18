package ru.yandex.practicum.kafka.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.kafka.telemetry.analyzer.domain.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {
}

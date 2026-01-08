package ru.yandex.practicum.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dal.entity.Payment;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}

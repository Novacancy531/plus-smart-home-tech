package ru.yandex.practicum.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.dal.entity.Delivery;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
    Optional<Delivery> findByOrderId(UUID orderId);

    @Query("select d.deliveryId from Delivery d where d.orderId = :id")
    Optional<UUID> findDeliveryUUIDByOrderUUID(UUID id);
}

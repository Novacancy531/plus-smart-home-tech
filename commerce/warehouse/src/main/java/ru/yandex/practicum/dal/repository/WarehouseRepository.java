package ru.yandex.practicum.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dal.entity.WarehouseProduct;

import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<WarehouseProduct, UUID> {
}

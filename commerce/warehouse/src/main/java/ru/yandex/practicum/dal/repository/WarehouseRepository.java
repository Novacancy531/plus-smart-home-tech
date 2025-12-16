package ru.yandex.practicum.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dal.entity.WarehouseProduct;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<WarehouseProduct, UUID> {

    List<WarehouseProduct> findAllByProductIdIn(Set<UUID> productIds);
}

package ru.yandex.practicum.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dal.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findAllByUsername(String username);
}

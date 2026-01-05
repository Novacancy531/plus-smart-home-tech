package ru.yandex.practicum.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.dal.entity.Payment;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PaymentMapper {

    PaymentDto toDto(Payment payment);

    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orderId", source = "orderId")
    Payment fromCalculated(
            UUID orderId,
            BigDecimal productTotal,
            BigDecimal deliveryTotal,
            BigDecimal feeTotal,
            BigDecimal totalPayment,
            PaymentStatus status
    );
}

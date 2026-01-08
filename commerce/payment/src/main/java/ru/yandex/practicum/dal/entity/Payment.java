package ru.yandex.practicum.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "payments")
public class Payment {

    @Id
    @Column(name = "payment_id", nullable = false, updatable = false)
    private UUID paymentId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "product_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal productTotal;

    @Column(name = "delivery_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal deliveryTotal;

    @Column(name = "fee_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal feeTotal;

    @Column(name = "total_payment", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPayment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (paymentId == null) paymentId = UUID.randomUUID();
        if (createdAt == null) createdAt = Instant.now();
        if (status == null) status = PaymentStatus.PENDING;
    }
}

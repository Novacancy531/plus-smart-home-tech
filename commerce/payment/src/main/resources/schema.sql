CREATE SCHEMA IF NOT EXISTS payment;

CREATE TABLE IF NOT EXISTS payment.payments
(
    payment_id     UUID PRIMARY KEY,
    order_id       UUID        NOT NULL,
    product_total  NUMERIC(19,2) NOT NULL,
    delivery_total NUMERIC(19,2) NOT NULL,
    fee_total      NUMERIC(19,2) NOT NULL,
    total_payment  NUMERIC(19,2) NOT NULL,
    status         VARCHAR(20) NOT NULL,
    created_at     TIMESTAMP   NOT NULL
);

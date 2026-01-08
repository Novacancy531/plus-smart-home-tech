CREATE SCHEMA IF NOT EXISTS delivery;

CREATE TABLE IF NOT EXISTS delivery.deliveries
(
    delivery_id     UUID PRIMARY KEY,
    order_id        UUID NOT NULL,

    from_country    VARCHAR(255) NOT NULL,
    from_city       VARCHAR(255) NOT NULL,
    from_street     VARCHAR(255) NOT NULL,
    from_house      VARCHAR(50)  NOT NULL,
    from_flat       VARCHAR(50),

    to_country      VARCHAR(255) NOT NULL,
    to_city         VARCHAR(255) NOT NULL,
    to_street       VARCHAR(255) NOT NULL,
    to_house        VARCHAR(50)  NOT NULL,
    to_flat         VARCHAR(50),

    delivery_state  VARCHAR(30)  NOT NULL,
    created_at      TIMESTAMP    NOT NULL
    );

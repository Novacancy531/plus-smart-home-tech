CREATE SCHEMA IF NOT EXISTS commerce;

CREATE TABLE IF NOT EXISTS commerce.orders
(
    order_id         UUID PRIMARY KEY,
    username         VARCHAR(255),
    shopping_cart_id UUID,
    payment_id       UUID,
    delivery_id      UUID,
    state            VARCHAR(30) NOT NULL,
    delivery_weight  DOUBLE PRECISION,
    delivery_volume  DOUBLE PRECISION,
    fragile           BOOLEAN,
    total_price      NUMERIC(19, 2),
    delivery_price   NUMERIC(19, 2),
    product_price    NUMERIC(19, 2),
    created_at       TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS commerce.order_products
(
    order_id   UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity   BIGINT NOT NULL,
    CONSTRAINT pk_order_products PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_order_products_order
    FOREIGN KEY (order_id)
    REFERENCES orders (order_id)
    ON DELETE CASCADE
    );

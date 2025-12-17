CREATE SCHEMA IF NOT EXISTS warehouse;

CREATE TABLE IF NOT EXISTS warehouse.dimensions
(
    id     uuid primary key,
    width  double precision not null,
    height double precision not null,
    depth  double precision not null
);

CREATE TABLE IF NOT EXISTS warehouse.warehouse_products
(
    product_id   uuid primary key,
    fragile      boolean          not null,
    weight       double precision not null,
    quantity     bigint           not null,
    dimension_id uuid             not null,

    constraint fk_warehouse_product_dimension
        foreign key (dimension_id)
            references warehouse.dimensions (id)
            on delete cascade
);

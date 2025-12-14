CREATE SCHEMA IF NOT EXISTS warehouse;


CREATE TABLE IF NOT EXISTS warehouse.warehouse_products
(
    product_id uuid primary key,
    fragile    boolean          not null,
    weight     double precision not null,
    quantity   bigint           not null,
    version    bigint           not null default 0
);

CREATE TABLE IF NOT EXISTS warehouse.dimensions
(
    product_id uuid primary key,
    width      double precision not null,
    height     double precision not null,
    depth      double precision not null,
    constraint fk_dimensions_product
        foreign key (product_id)
            references warehouse.warehouse_products (product_id)
            on delete cascade
);

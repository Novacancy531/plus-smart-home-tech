CREATE SCHEMA IF NOT EXISTS cart;

CREATE TABLE IF NOT EXISTS cart.shopping_carts
(
    shopping_cart_id uuid primary key default gen_random_uuid(),
    username         varchar(255) not null,
    active           boolean      not null
);

CREATE TABLE IF NOT EXISTS cart.cart_products
(
    shopping_cart_id uuid   not null,
    product_id       uuid   not null,
    quantity         bigint not null,

    constraint pk_cart_products primary key (shopping_cart_id, product_id),
    constraint fk_cart_products_cart
        foreign key (shopping_cart_id)
            references cart.shopping_carts (shopping_cart_id)
            on delete cascade
);

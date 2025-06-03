-- Create schema
CREATE SCHEMA IF NOT EXISTS public AUTHORIZATION pg_database_owner;

COMMENT ON SCHEMA public IS 'standard public schema';

-- Sequences
CREATE SEQUENCE IF NOT EXISTS delivery_id_seq INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS menu_id_seq INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS order_item_id_seq INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS orders_id_seq INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS reservation_id_seq INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS restaurant_table_id_seq INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS users_id_seq INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE;

-- Tables

CREATE TABLE IF NOT EXISTS menu (
                                    id BIGINT PRIMARY KEY DEFAULT NEXTVAL('menu_id_seq'),
    allergens VARCHAR(255),
    description VARCHAR(255),
    name VARCHAR(255),
    price NUMERIC(38, 2)
    );

CREATE TABLE IF NOT EXISTS restaurant_table (
                                                id BIGINT PRIMARY KEY DEFAULT NEXTVAL('restaurant_table_id_seq'),
    seats_number INT NOT NULL,
    table_number INT NOT NULL
    );

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT PRIMARY KEY DEFAULT NEXTVAL('users_id_seq'),
    address VARCHAR(255),
    email VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    password VARCHAR(255),
    phone_number VARCHAR(255),
    role VARCHAR(255),
    username VARCHAR(255),
    CONSTRAINT users_role_check CHECK (role IN ('ROLE_ADMINISTRATOR', 'ROLE_USER'))
    );

CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT PRIMARY KEY DEFAULT NEXTVAL('orders_id_seq'),
    delivery_type VARCHAR(255),
    order_time TIMESTAMP,
    status VARCHAR(255),
    total_price NUMERIC(38, 2),
    user_id BIGINT,
    CONSTRAINT orders_delivery_type_check CHECK (delivery_type IN ('ON_SITE', 'DELIVERY')),
    CONSTRAINT orders_status_check CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS reservation (
                                           id BIGINT PRIMARY KEY DEFAULT NEXTVAL('reservation_id_seq'),
    end_time TIMESTAMP,
    start_time TIMESTAMP,
    table_id BIGINT,
    user_id BIGINT,
    CONSTRAINT fk_res_table FOREIGN KEY (table_id) REFERENCES restaurant_table(id),
    CONSTRAINT fk_res_user FOREIGN KEY (user_id) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS delivery (
                                        id BIGINT PRIMARY KEY DEFAULT NEXTVAL('delivery_id_seq'),
    address VARCHAR(255),
    delivery_date TIMESTAMP,
    status VARCHAR(255),
    order_id BIGINT UNIQUE,
    CONSTRAINT delivery_status_check CHECK (status IN ('IN_PROGRESS', 'DELIVERED', 'CANCELED')),
    CONSTRAINT fk_delivery_order FOREIGN KEY (order_id) REFERENCES orders(id)
    );

CREATE TABLE IF NOT EXISTS order_item (
                                          id BIGINT PRIMARY KEY DEFAULT NEXTVAL('order_item_id_seq'),
    item_price NUMERIC(38, 2),
    quantity INT NOT NULL,
    item_id BIGINT,
    order_id BIGINT,
    CONSTRAINT fk_item_menu FOREIGN KEY (item_id) REFERENCES menu(id),
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id)
    );

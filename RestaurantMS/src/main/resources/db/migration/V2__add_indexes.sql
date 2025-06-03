-- V2__add_indexes.sql
CREATE UNIQUE INDEX IF NOT EXISTS uk3bdrbd2jcybaaa5rxkj4s7vlk ON delivery (order_id);

CREATE UNIQUE INDEX IF NOT EXISTS menu_pkey ON menu (id);
CREATE UNIQUE INDEX IF NOT EXISTS order_item_pkey ON order_item (id);
CREATE UNIQUE INDEX IF NOT EXISTS orders_pkey ON orders (id);
CREATE UNIQUE INDEX IF NOT EXISTS reservation_pkey ON reservation (id);
CREATE UNIQUE INDEX IF NOT EXISTS restaurant_table_pkey ON restaurant_table (id);
CREATE UNIQUE INDEX IF NOT EXISTS users_pkey ON users (id);
CREATE UNIQUE INDEX IF NOT EXISTS delivery_pkey ON delivery (id);

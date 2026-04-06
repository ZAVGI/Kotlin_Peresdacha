CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL
);
CREATE INDEX idx_users_role ON users(role);

CREATE TABLE products (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  price NUMERIC(10,2) NOT NULL,
  stock INT NOT NULL
);
CREATE INDEX idx_products_name ON products(name);

CREATE TABLE orders (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);

CREATE TABLE order_items (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  product_id BIGINT NOT NULL REFERENCES products(id),
  quantity INT NOT NULL,
  price_snapshot NUMERIC(10,2) NOT NULL
);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);

CREATE TABLE audit_logs (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT,
  action VARCHAR(100) NOT NULL,
  payload TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_user ON audit_logs(user_id);

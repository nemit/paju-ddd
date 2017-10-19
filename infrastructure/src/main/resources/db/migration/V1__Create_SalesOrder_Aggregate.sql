CREATE TABLE sales_order (
  id UUID PRIMARY KEY,
  customer_id UUID,
  confirmed BOOLEAN,
  deleted BOOLEAN
);

CREATE TABLE person (
  id BIGSERIAL PRIMARY KEY,
  first_name TEXT,
  last_name TEXT,
  sex VARCHAR(30),
  date_of_birth DATE
);

CREATE TABLE person_role_in_sales_order (
  sales_order_id UUID REFERENCES sales_order ON DELETE CASCADE,
  person_id BIGINT REFERENCES person ON DELETE CASCADE,
  role VARCHAR(255)
);

CREATE TABLE reserved_service (
  id UUID PRIMARY KEY,
  name TEXT,
  description TEXT,
  price FLOAT,
  price_currency VARCHAR(255),
  price_vat VARCHAR(255),
  service_id UUID,
  date_start DATE,
  date_end DATE
);

CREATE TABLE reserved_services_in_sales_order (
  reserved_service_id UUID REFERENCES reserved_service ON DELETE CASCADE,
  sales_order_id UUID REFERENCES sales_order ON DELETE CASCADE,
  payment_status VARCHAR(30),
  payment_method VARCHAR(30),
  delivery_status VARCHAR(30),
  PRIMARY KEY (reserved_service_id, sales_order_id)
);

CREATE TABLE product (
  id BIGSERIAL PRIMARY KEY,
  name TEXT,
  description TEXT,
  price FLOAT,
  price_currency VARCHAR(255),
  price_vat VARCHAR(255)
);

CREATE TABLE products_in_sales_order (
  sales_order_id UUID REFERENCES sales_order ON DELETE CASCADE,
  product_id BIGINT REFERENCES product ON DELETE CASCADE,
  payment_status VARCHAR(30),
  payment_method VARCHAR(30),
  delivery_status VARCHAR(30)
);



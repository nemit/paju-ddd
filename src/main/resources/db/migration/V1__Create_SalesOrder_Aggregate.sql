CREATE TABLE sales_order (
  id UUID PRIMARY KEY,
  customer_id UUID,
  confirmed BOOLEAN,
  deleted BOOLEAN
);

CREATE TABLE person (
  id BIGSERIAL PRIMARY KEY,
  first_name TEXT,
  last_name TEXT
);

CREATE TABLE person_role_in_sales_order (
  sales_order_id UUID REFERENCES sales_order,
  person_id BIGINT REFERENCES person,
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
  reserved_service_id UUID REFERENCES reserved_service,
  sales_order_id UUID REFERENCES sales_order,
  payment_status VARCHAR(10),
  payment_method VARCHAR(10),
  delivery_status VARCHAR(10),
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
  sales_order_id UUID REFERENCES sales_order,
  product_id BIGINT REFERENCES product,
  payment_status VARCHAR(10),
  payment_method VARCHAR(10),
  delivery_status VARCHAR(10)
);


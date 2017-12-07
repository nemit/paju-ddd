CREATE TABLE sales_order (
  id UUID PRIMARY KEY,
  customer_id UUID,
  confirmed BOOLEAN,
  deleted BOOLEAN
);

CREATE TABLE product (
  sales_order_id UUID REFERENCES sales_order ON DELETE CASCADE,
  id UUID,
  name TEXT,
  description TEXT,
  price FLOAT,
  price_currency VARCHAR(255),
  price_vat VARCHAR(255),
  payment_status VARCHAR(30),
  payment_method VARCHAR(30),
  delivery_status VARCHAR(30),
  PRIMARY KEY (sales_order_id, id)
);

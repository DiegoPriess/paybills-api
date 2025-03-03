CREATE TABLE bills (

    id TEXT PRIMARY KEY UNIQUE NOT NULL,
    due_date DATE NOT NULL,
    payment_date DATE,
    amount NUMERIC(19, 2) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(10) NOT NULL,
    user_id TEXT NOT NULL,
    CONSTRAINT fk_bills_user FOREIGN KEY (user_id) REFERENCES users(id)

);

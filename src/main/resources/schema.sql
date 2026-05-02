CREATE TABLE IF NOT EXISTS Customers(
	customer_id SERIAL PRIMARY KEY,
	customer_name VARCHAR(100) NOT NULL,
	last_name VARCHAR(150) NOT NULL,
	dni VARCHAR(20) NOT NULL,
	email VARCHAR(150) NOT NULL,
	phone_number VARCHAR (20) NOT NULL,
	creation_date TIMESTAMP,
	UNIQUE (dni, email, phone_number)
);

CREATE TABLE IF NOT EXISTS Accounts(
	account_id SERIAL PRIMARY KEY,
	account_number VARCHAR(34) UNIQUE NOT NULL,
	account_holder VARCHAR(250) NOT NULL,
	balance NUMERIC(15,2) DEFAULT 0,
	creation_date TIMESTAMP,
	customer_id INT,
	FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);

CREATE TABLE IF NOT EXISTS Transactions(
	transaction_id SERIAL PRIMARY KEY,
	transaction_type VARCHAR(50) CHECK(transaction_type IN('DEPOSITO', 'RETIRO', 'TRANSFERENCIA_SALIENTE', 'TRANSFERENCIA_ENTRANTE')),
	amount NUMERIC(15,2) CHECK(amount > 0),
	description VARCHAR(50),
	creation_date TIMESTAMP,
	account_id INT,
	FOREIGN KEY (account_id) REFERENCES Accounts(account_id)
);
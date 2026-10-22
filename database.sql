CREATE DATABASE IF NOT EXISTS bank CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bank;

CREATE TABLE IF NOT EXISTS accounts (
    account_number BIGINT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    pin INT NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_number BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_transactions_account_date (account_number, transaction_date),
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);
USE bank;
SHOW TABLES;

-- Fish Waste Marketplace Database Schema

CREATE DATABASE IF NOT EXISTS fish_waste_db;
USE fish_waste_db;

CREATE TABLE IF NOT EXISTS seller (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS company (
    company_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'COMPANY',
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS admin (
    email_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS waste_detail (
    waste_id INT AUTO_INCREMENT PRIMARY KEY,
    waste_type VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    seller_detail TEXT,
    location VARCHAR(200),
    status VARCHAR(50) DEFAULT 'AVAILABLE',
    user_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES seller(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS company_requirement (
    requirement_id INT AUTO_INCREMENT PRIMARY KEY,
    waste_type VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    location VARCHAR(200),
    budget DECIMAL(12,2),
    company_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    waste_id INT,
    company_id INT,
    requirement_id INT,
    status VARCHAR(50) DEFAULT 'PENDING',
    request_date DATE DEFAULT (CURDATE()),
    FOREIGN KEY (waste_id) REFERENCES waste_detail(waste_id),
    FOREIGN KEY (company_id) REFERENCES company(company_id),
    FOREIGN KEY (requirement_id) REFERENCES company_requirement(requirement_id)
);

-- Default admin
INSERT IGNORE INTO admin (name, email, password) VALUES ('Admin', 'admin@fishwaste.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhe'); -- password: admin123

CREATE DATABASE flowerManagement_db;
USE flowerManagement_db;

CREATE TABLE Users (
    userId VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role ENUM('Customer', 'Administrator', 'user') NOT NULL
);

CREATE TABLE Customer (
    userId VARCHAR(50) PRIMARY KEY,
    FOREIGN KEY (userId) REFERENCES Users(userId)
);

CREATE TABLE Administrator (
    userId VARCHAR(50) PRIMARY KEY,
    FOREIGN KEY (userId) REFERENCES Users(userId)
);

CREATE TABLE Flower (
    flowerId INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    description TEXT,
    price DECIMAL(10, 2),
    season VARCHAR(50),
    stock INT,
    image TEXT
);

CREATE TABLE Bouquet (
    bouquetId INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    description TEXT,
    price DECIMAL(10, 2),
    image TEXT
);

CREATE TABLE BouquetFlowers (
    bouquetId INT,
    flowerId INT,
    FOREIGN KEY (bouquetId) REFERENCES Bouquet(bouquetId),
    FOREIGN KEY (flowerId) REFERENCES Flower(flowerId),
    PRIMARY KEY (bouquetId, flowerId)
);

CREATE TABLE Cart (
    cartId INT PRIMARY KEY AUTO_INCREMENT,
    customerId VARCHAR(50),
    FOREIGN KEY (customerId) REFERENCES Customer(userId)
);

CREATE TABLE CartItem (
    itemId INT PRIMARY KEY AUTO_INCREMENT,
    cartId INT,
    itemType ENUM('Flower', 'Bouquet') NOT NULL,
    itemRefId INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (cartId) REFERENCES Cart(cartId)
);

CREATE TABLE `Order` (
    orderId VARCHAR(50) PRIMARY KEY,
    customerId VARCHAR(50),
    total DECIMAL(10, 2),
    status VARCHAR(50),
    orderDate DATE,
    FOREIGN KEY (customerId) REFERENCES Customer(userId)
);

CREATE TABLE OrderItems (
    itemId INT PRIMARY KEY AUTO_INCREMENT,
    orderId VARCHAR(50),
    itemType ENUM('Flower', 'Bouquet') NOT NULL,
    itemRefId INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (orderId) REFERENCES `Order`(orderId)
);

CREATE TABLE Receipt (
    receiptId INT PRIMARY KEY AUTO_INCREMENT,
    orderId VARCHAR(50),
    issueDate DATE,
    total DECIMAL(10, 2),
    FOREIGN KEY (orderId) REFERENCES `Order`(orderId)
);

CREATE TABLE OrderUpdates (
    updateId INT PRIMARY KEY AUTO_INCREMENT,
    orderId VARCHAR(50),
    adminId VARCHAR(50),
    updateNote TEXT,
    updateDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (orderId) REFERENCES `Order`(orderId),
    FOREIGN KEY (adminId) REFERENCES Administrator(userId)
);

CREATE TABLE password_reset_tokens (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    token VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    UNIQUE KEY (token)
);

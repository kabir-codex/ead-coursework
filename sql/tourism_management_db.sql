-- ============================================================
-- TourEase Hotel & Tourism Management System
-- Database Setup Script
-- tourism_management_db
-- ============================================================

CREATE DATABASE IF NOT EXISTS tourism_management_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE tourism_management_db;

-- ============================================================
-- TABLE: users
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    full_name   VARCHAR(100) NOT NULL,
    role        ENUM('ADMIN','RECEPTIONIST') NOT NULL DEFAULT 'RECEPTIONIST',
    email       VARCHAR(100),
    is_active   TINYINT(1) NOT NULL DEFAULT 1,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_role CHECK (role IN ('ADMIN','RECEPTIONIST'))
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: customers
-- ============================================================
CREATE TABLE IF NOT EXISTS customers (
    customer_id     INT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(100) NOT NULL,
    nic_passport    VARCHAR(50)  NOT NULL UNIQUE,
    contact_number  VARCHAR(20)  NOT NULL,
    email           VARCHAR(100),
    address         TEXT,
    nationality     VARCHAR(50),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: packages
-- ============================================================
CREATE TABLE IF NOT EXISTS packages (
    package_id      INT AUTO_INCREMENT PRIMARY KEY,
    package_name    VARCHAR(100) NOT NULL,
    destination     VARCHAR(100) NOT NULL,
    duration_days   INT          NOT NULL,
    price           DECIMAL(10,2) NOT NULL,
    description     TEXT,
    status          ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_price_positive    CHECK (price >= 0),
    CONSTRAINT chk_duration_positive CHECK (duration_days > 0)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: rooms
-- ============================================================
CREATE TABLE IF NOT EXISTS rooms (
    room_id         INT AUTO_INCREMENT PRIMARY KEY,
    room_number     VARCHAR(10)  NOT NULL UNIQUE,
    room_type       ENUM('SINGLE','DOUBLE','SUITE','DELUXE') NOT NULL,
    capacity        INT          NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    availability    ENUM('AVAILABLE','BOOKED','MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
    CONSTRAINT chk_room_price    CHECK (price_per_night >= 0),
    CONSTRAINT chk_room_capacity CHECK (capacity > 0)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: bookings
-- ============================================================
CREATE TABLE IF NOT EXISTS bookings (
    booking_id      INT AUTO_INCREMENT PRIMARY KEY,
    customer_id     INT NOT NULL,
    package_id      INT,
    room_id         INT NOT NULL,
    booking_date    DATE         NOT NULL,
    checkin_date    DATE         NOT NULL,
    checkout_date   DATE         NOT NULL,
    total_amount    DECIMAL(10,2) NOT NULL,
    status          ENUM('CONFIRMED','PENDING','CANCELLED','COMPLETED') NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE RESTRICT,
    CONSTRAINT fk_booking_package  FOREIGN KEY (package_id)  REFERENCES packages(package_id)  ON DELETE SET NULL,
    CONSTRAINT fk_booking_room     FOREIGN KEY (room_id)     REFERENCES rooms(room_id)         ON DELETE RESTRICT,
    CONSTRAINT chk_dates           CHECK (checkout_date > checkin_date),
    CONSTRAINT chk_total_positive  CHECK (total_amount >= 0)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: payments
-- ============================================================
CREATE TABLE IF NOT EXISTS payments (
    payment_id      INT AUTO_INCREMENT PRIMARY KEY,
    booking_id      INT          NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    payment_method  ENUM('CASH','CARD','BANK_TRANSFER','ONLINE') NOT NULL DEFAULT 'CASH',
    payment_date    DATE         NOT NULL,
    notes           TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE RESTRICT,
    CONSTRAINT chk_payment_amount CHECK (amount > 0)
) ENGINE=InnoDB;

-- ============================================================
-- SAMPLE DATA
-- ============================================================

-- Users (password stored as plain text here; hash in production)
INSERT INTO users (username, password, full_name, role, email) VALUES
('admin',        'admin123',      'System Administrator', 'ADMIN',        'admin@tourease.lk'),
('receptionist', 'recept123',     'Sarah Fernando',       'RECEPTIONIST', 'sarah@tourease.lk'),
('manager',      'manager123',    'Kasun Perera',         'ADMIN',        'kasun@tourease.lk');

-- Customers
INSERT INTO customers (full_name, nic_passport, contact_number, email, address, nationality) VALUES
('Nimal Perera',      '199012345678', '0771234567', 'nimal@email.com',   'No 12, Galle Rd, Colombo 03',  'Sri Lankan'),
('Jane Smith',        'A12345678',    '0777654321', 'jane@email.com',    '45 Oxford St, London',          'British'),
('Rajesh Kumar',      'P9876543',     '0712345678', 'rajesh@email.com',  'Mumbai, India',                 'Indian'),
('Maria Garcia',      'ES1234567',    '0762345678', 'maria@email.com',   'Madrid, Spain',                 'Spanish'),
('David Chen',        'HK9876543',    '0752345678', 'david@email.com',   'Hong Kong',                     'Chinese'),
('Amara Silva',       '199512367890', '0781234567', 'amara@email.com',   'Kandy, Sri Lanka',              'Sri Lankan'),
('Thomas Brown',      'US1234567',    '0723456789', 'thomas@email.com',  'New York, USA',                 'American'),
('Fatima Al-Hassan',  'AE9876543',    '0743456789', 'fatima@email.com',  'Dubai, UAE',                    'Emirati');

-- Packages
INSERT INTO packages (package_name, destination, duration_days, price, description, status) VALUES
('Sigiriya & Cultural Triangle',  'Sigiriya, Polonnaruwa, Anuradhapura', 5, 45000.00, 'Explore ancient kingdoms and UNESCO heritage sites', 'ACTIVE'),
('Southern Coast Paradise',       'Galle, Mirissa, Tangalle',            4, 38000.00, 'Beach, whale watching and Dutch fort experience',   'ACTIVE'),
('Hill Country Escape',           'Nuwara Eliya, Ella, Haputale',        6, 52000.00, 'Tea plantations, waterfalls and scenic train rides', 'ACTIVE'),
('Wildlife & Elephant Safari',    'Yala, Udawalawe, Minneriya',          4, 42000.00, 'Spot leopards, elephants and exotic bird species',   'ACTIVE'),
('Luxury Colombo City Tour',      'Colombo',                             2, 18000.00, 'Shopping, dining and urban cultural experience',     'ACTIVE'),
('Complete Sri Lanka',            'Island Wide',                         14,120000.00,'Comprehensive island tour covering all highlights',  'ACTIVE');

-- Rooms
INSERT INTO rooms (room_number, room_type, capacity, price_per_night, availability) VALUES
('101', 'SINGLE', 1, 5000.00,  'AVAILABLE'),
('102', 'SINGLE', 1, 5000.00,  'AVAILABLE'),
('201', 'DOUBLE', 2, 8000.00,  'AVAILABLE'),
('202', 'DOUBLE', 2, 8000.00,  'BOOKED'),
('203', 'DOUBLE', 2, 8500.00,  'AVAILABLE'),
('301', 'DELUXE', 2, 12000.00, 'AVAILABLE'),
('302', 'DELUXE', 3, 13000.00, 'AVAILABLE'),
('401', 'SUITE',  4, 22000.00, 'AVAILABLE'),
('402', 'SUITE',  4, 25000.00, 'MAINTENANCE'),
('103', 'SINGLE', 1, 5500.00,  'AVAILABLE');

-- Bookings
INSERT INTO bookings (customer_id, package_id, room_id, booking_date, checkin_date, checkout_date, total_amount, status) VALUES
(1, 1, 3, '2025-01-10', '2025-01-15', '2025-01-20', 85000.00, 'COMPLETED'),
(2, 2, 6, '2025-02-01', '2025-02-10', '2025-02-14', 80000.00, 'COMPLETED'),
(3, 3, 7, '2025-03-05', '2025-03-15', '2025-03-21', 130000.00,'CONFIRMED'),
(4, 4, 8, '2025-03-10', '2025-04-01', '2025-04-05', 130000.00,'CONFIRMED'),
(5, 5, 1, '2025-03-12', '2025-03-20', '2025-03-22', 28000.00, 'PENDING'),
(6, 6, 8, '2025-04-01', '2025-05-01', '2025-05-15', 300000.00,'PENDING'),
(7, 1, 3, '2025-04-05', '2025-04-20', '2025-04-25', 85000.00, 'CONFIRMED'),
(8, 2, 5, '2025-04-10', '2025-04-25', '2025-04-29', 76000.00, 'CONFIRMED');

-- Payments
INSERT INTO payments (booking_id, amount, payment_method, payment_date, notes) VALUES
(1, 85000.00, 'CARD',          '2025-01-15', 'Full payment on check-in'),
(2, 40000.00, 'CASH',          '2025-02-10', 'Deposit payment'),
(2, 40000.00, 'BANK_TRANSFER', '2025-02-14', 'Balance on checkout'),
(3, 65000.00, 'ONLINE',        '2025-03-05', 'Advance payment via website'),
(4, 65000.00, 'CARD',          '2025-03-10', 'Deposit 50%'),
(5, 28000.00, 'CASH',          '2025-03-20', 'Full payment cash');

-- ============================================================
-- END OF SCRIPT
-- ============================================================

CREATE TABLE stations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    telecode CHAR(3) NOT NULL,
    name VARCHAR(30) NOT NULL
);

CREATE TABLE trains (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    style VARCHAR(30) NOT NULL
);

CREATE TABLE train_stations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    station_id BIGINT NOT NULL,
    train_code VARCHAR(10) NOT NULL,
    sequence INT NOT NULL,
    arrive_day INT NOT NULL DEFAULT 0,
    arrive_time TIME NOT NULL,
    start_day INT NOT NULL DEFAULT 0,
    start_time TIME NOT NULL
);

CREATE TABLE prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_station_id BIGINT NOT NULL,
    to_station_id BIGINT NOT NULL,
    train_id BIGINT NOT NULL,
    price_raw VARCHAR(100) NOT NULL
);

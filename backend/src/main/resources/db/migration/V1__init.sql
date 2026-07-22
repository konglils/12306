-- 列车
CREATE TABLE cars (
    id    BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    style VARCHAR(30) NOT NULL,
    code  VARCHAR(30) NOT NULL
);

-- 区域
CREATE TABLE areas (
    id   BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL
);

-- 车次
CREATE TABLE trains (
    id     BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(30) NOT NULL,
    UNIQUE KEY (number)
);

-- 车站
CREATE TABLE stations (
    id         BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    area_id    BIGINT      NOT NULL,
    telecode   CHAR(3)     NOT NULL,
    name       VARCHAR(30) NOT NULL,
    UNIQUE KEY (telecode)
);

-- 某天某车次的列车和布局
CREATE TABLE car_layouts (
    train_date DATE   NOT NULL,
    train_id   BIGINT NOT NULL,
    car_id     BIGINT,
    layout     JSON   NOT NULL,
    PRIMARY KEY (train_date, train_id)
);

-- 某天时刻表
CREATE TABLE stops (
    train_date  DATE        NOT NULL,
    train_id    BIGINT      NOT NULL,
    stop_idx    SMALLINT    NOT NULL,
    station_id  BIGINT      NOT NULL,
    train_code  VARCHAR(10) NOT NULL,
    arrive_day  TINYINT     NOT NULL,
    arrive_time TIME,
    start_day   TINYINT     NOT NULL,
    start_time  TIME,
    PRIMARY KEY (train_date, train_id, stop_idx)
);

-- 某天某种有座类型在第某段的座位图
CREATE TABLE seats (
    train_date  DATE           NOT NULL,
    train_id    BIGINT         NOT NULL,
    seat_type   CHAR(1)        NOT NULL,
    segment_idx SMALLINT       NOT NULL,
    graph       VARBINARY(255) NOT NULL,
    PRIMARY KEY (train_date, train_id, seat_type, segment_idx)
);

-- 某天某种无座类型在第某段的余票数量
CREATE TABLE no_seats (
    train_date  DATE       NOT NULL,
    train_id    BIGINT     NOT NULL,
    seat_type   CHAR(1)    NOT NULL,
    segment_idx SMALLINT   NOT NULL,
    remaining   SMALLINT   NOT NULL,
    PRIMARY KEY (train_date, train_id, seat_type, segment_idx)
);

-- 某天某车次从X站到Y站的某种座位类型的价格
CREATE TABLE prices (
    train_date      DATE       NOT NULL,
    from_area_id    BIGINT     NOT NULL,
    to_area_id      BIGINT     NOT NULL,
    train_id        BIGINT     NOT NULL,
    from_station_id BIGINT     NOT NULL,
    to_station_id   BIGINT     NOT NULL,
    from_stop_idx   SMALLINT   NOT NULL,
    to_stop_idx     SMALLINT   NOT NULL,
    seat_type       CHAR(1)    NOT NULL,
    has_seat        BOOLEAN    NOT NULL,
    price           MEDIUMINT  NOT NULL,
    PRIMARY KEY (train_date, train_id, from_station_id, to_station_id, seat_type, has_seat)
);

-- 查询车次
CREATE INDEX idx_prices_area ON prices (train_date, from_area_id, to_area_id);

-- 用户
CREATE TABLE users (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(30)  NOT NULL,
    password      VARCHAR(255) NOT NULL,
    session_token VARCHAR(64),
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY (username)
);

-- 乘车人
CREATE TABLE passengers (
    user_id       BIGINT      NOT NULL,
    is_user       BOOLEAN     NOT NULL,
    id_type       TINYINT     NOT NULL,
    id_no         VARCHAR(60) NOT NULL,
    name          VARCHAR(60) NOT NULL,
    phone_e164    VARCHAR(20),
    email         VARCHAR(255),
    country_code  CHAR(3),
    birth_date    DATE,
    sex           CHAR(1),
    valid_through DATE,
    discount_type TINYINT     NOT NULL,
    status        TINYINT     NOT NULL,
    updated_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, id_type, id_no)
);

-- 订单
CREATE TABLE orders (
    id              BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT   NOT NULL,
    train_date      DATE     NOT NULL,
    train_id        BIGINT   NOT NULL,
    from_station_id BIGINT   NOT NULL,
    to_station_id   BIGINT   NOT NULL,
    status          TINYINT  NOT NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expire_at       DATETIME NOT NULL,
    paid_at         DATETIME
);

-- 查询用户历史订单
CREATE INDEX idx_orders_user ON orders (user_id);

-- 每个乘车人的订单
CREATE TABLE passenger_orders (
    id            BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id      BIGINT      NOT NULL,
    id_type       TINYINT     NOT NULL,
    id_no         VARCHAR(60) NOT NULL,
    name          VARCHAR(60) NOT NULL,
    phone_e164    VARCHAR(20),
    email         VARCHAR(255),
    country_code  CHAR(3),
    birth_date    DATE,
    sex           CHAR(1),
    valid_through DATE,
    discount_type TINYINT     NOT NULL,
    seat_type     CHAR(1)     NOT NULL,
    has_seat      BOOLEAN     NOT NULL,
    seat_idx      SMALLINT    NOT NULL,
    seat_name     VARCHAR(30) NOT NULL,
    price         MEDIUMINT   NOT NULL,
    refunded      BOOLEAN     NOT NULL DEFAULT FALSE,
    refund_time   DATETIME,
    refund_price  MEDIUMINT,
    UNIQUE KEY (order_id, id_type, id_no)
);

-- 查询订单明细
CREATE INDEX idx_passenger_orders ON passenger_orders (order_id);

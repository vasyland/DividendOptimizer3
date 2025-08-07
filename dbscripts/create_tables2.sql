-- Security Tables
-- User Info 
CREATE TABLE `user_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email_id` varchar(255) NOT NULL,
  `mobile_number` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `roles` varchar(255) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKeo44j61iq2l3i834bgn193qxr` (`email_id`)
);

-- 
CREATE TABLE `refresh_tokens` (
  `id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `refresh_token` varchar(10000) NOT NULL,
  `revoked` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_user_id` (`user_id`),
  CONSTRAINT `FK_user_info_id` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`)
);

-- 
CREATE TABLE `user_subscription` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `subscription_end_date` datetime DEFAULT NULL,
  `created_on` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_on` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_user_id` (`user_id`),
  CONSTRAINT `FK_user_info_id_subscription` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`)
);


-- Portfolio Management Tables



-- Current Price tables with data from fmp
use iwm;
CREATE TABLE fmp_current_price (
    symbol VARCHAR(20) NOT NULL,
    name VARCHAR(255),
    price DECIMAL(16, 8),
    changes_percentage DECIMAL(10, 6),
    price_change DECIMAL(16, 8),                -- `change` is a reserved word
    day_low DECIMAL(16, 8),
    day_high DECIMAL(16, 8),
    year_high DECIMAL(16, 8),
    year_low DECIMAL(16, 8),
    market_cap BIGINT,
    price_avg50 DECIMAL(16, 8),
    price_avg200 DECIMAL(16, 8),
    exchange VARCHAR(10),
    volume BIGINT,
    avg_volume BIGINT,
    open_price DECIMAL(16, 8),                  -- `open` is also reserved
    previous_close DECIMAL(16, 8),
    eps DECIMAL(16, 8),
    pe DECIMAL(16, 8),
    earnings_announcement DATETIME,
    shares_outstanding BIGINT,
    created_on DATETIME,                   -- `timestamp` is a reserved word
    INDEX idx_symbol (symbol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE fmp_history_price (
    symbol VARCHAR(20) NOT NULL,
    name VARCHAR(255),
    price DECIMAL(16, 8),
    changes_percentage DECIMAL(10, 6),
    price_change DECIMAL(16, 8),                -- `change` is a reserved word
    day_low DECIMAL(16, 8),
    day_high DECIMAL(16, 8),
    year_high DECIMAL(16, 8),
    year_low DECIMAL(16, 8),
    market_cap BIGINT,
    price_avg50 DECIMAL(16, 8),
    price_avg200 DECIMAL(16, 8),
    exchange VARCHAR(10),
    volume BIGINT,
    avg_volume BIGINT,
    open_price DECIMAL(16, 8),                  -- `open` is also reserved
    previous_close DECIMAL(16, 8),
    eps DECIMAL(16, 8),
    pe DECIMAL(16, 8),
    earnings_announcement DATETIME,
    shares_outstanding BIGINT,
    created_on DATETIME,                   -- `timestamp` is a reserved word
    INDEX idx_symbol (symbol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
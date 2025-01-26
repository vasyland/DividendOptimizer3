
USE golem;

use iwmdev;
-- Last Updated January 26, 20
DROP TABLE `user_subscription_seq`;
DROP TABLE `user_subscription`;
DROP TABLE `refresh_tokens_seq`;
DROP TABLE `refresh_tokens`;
DROP TABLE `user_info_seq`;
DROP TABLE `user_info`;

SHOW TABLES LIKE 'user_info_seq';

-- User 
CREATE TABLE `user_info` (
  `id` bigint NOT NULL,
  `email_id` varchar(60) NOT NULL COMMENT 'User login credentials',
  `mobile_number` varchar(30) DEFAULT NULL COMMENT 'Not in use and can be deleted for now.',
  `password` varchar(800) NOT NULL COMMENT 'User encrypted password',
  `roles` varchar(100) NOT NULL COMMENT 'User role.',
  `user_name` varchar(255) DEFAULT NULL COMMENT 'Not in use and can be deleted for now.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_email_id` (`email_id`)
);

CREATE TABLE `user_info_seq` (
  `next_val` BIGINT NOT NULL
);
INSERT INTO user_info_seq (next_val) VALUES (1);
commit;

-- User Refresh Tokens 
CREATE TABLE `refresh_tokens` (
  `id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `refresh_token` varchar(10000) NOT NULL,
  `revoked` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_user_id` (`user_id`),
  CONSTRAINT `FK_user_info_id` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`)
);

CREATE TABLE `refresh_tokens_seq` (
  `next_val` BIGINT NOT NULL
);
INSERT INTO refresh_tokens_seq (next_val) VALUES (1);
commit;


-- User Subscriptions
CREATE TABLE `user_subscription` (
  `id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `subscription_end_date` datetime,
  `created_on` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_on` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_user_id` (`user_id`),
  CONSTRAINT `FK_user_info_id_subscription` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`)
);

CREATE TABLE `user_subscription_seq` (
  `next_val` BIGINT NOT NULL
);
INSERT INTO user_subscription_seq (next_val) VALUES (1);
commit;




--  These tables are not hooked to user info table 
CREATE TABLE `volatility_date` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `day_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `action_description` varchar(450) DEFAULT NULL,
  `description` varchar(450) DEFAULT NULL,
  `active` int DEFAULT NULL,
  `active_from_date` datetime DEFAULT NULL,
  `active_to_date` datetime DEFAULT NULL,
  `created_on` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_on` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `volatility_date_seq` (
  `next_val` BIGINT NOT NULL
);
INSERT INTO volatility_date_seq (next_val) VALUES (1);

/**
 * Wheather recordings. This table is not used by the applicaiton yet.
 * It will be an additional featuyre of the applicaiton to compare 
 * stock price changes to the wheather changes: pressure, temperature, wind direction and speed.
 */
CREATE TABLE `weather_forecast` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `forecast_on` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'forecast time',
  `city` varchar(20) DEFAULT NULL,
  `air_pressure` decimal(7,3) DEFAULT NULL,
  `temperature` int DEFAULT NULL,
  `humidity` int DEFAULT NULL,
  `feels_like` int DEFAULT NULL,
  `wind_direction` varchar(3) DEFAULT NULL,
  `wind_speed` int DEFAULT NULL,
  `created_on` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'creation time',
  `updated_on` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `well_being` varchar(45) DEFAULT NULL,
  `hawks_seen` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
);



/** ============================================================================ */





DROP TABLE `marketing_symbol_status`;
CREATE TABLE `marketing_symbol_status` (
  `symbol` varchar(10) NOT NULL COMMENT 'Stock ticker. TSX with .TO',
  `current_price` decimal(10,4) DEFAULT NULL COMMENT 'Cuurent price taken from yahoo finance',
  `quoterly_dividend_amount` decimal(10,4) DEFAULT NULL COMMENT 'Majority of comapnies pay dividends on a quaterly basis',
  `current_yield` decimal(6,4) DEFAULT NULL COMMENT 'Current yeild calculated from (quaterly_dividend_amount * 4) /current_price * 100',
  `allowed_buy_price` decimal(10,2) DEFAULT NULL COMMENT 'Price just below top yiled price. ',
  `best_buy_price` decimal(10,2) DEFAULT NULL COMMENT 'Best price to buy. It is at the top of the yield',
  `updated_on` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`symbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



CREATE TABLE `scenario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `scenario_name` varchar(45) DEFAULT NULL COMMENT 'Some meaningfull description or name',
  `invested_amount` decimal(16,2) DEFAULT NULL,
  `created_on` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_on` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_scenario_user1_idx` (`user_id`),
  CONSTRAINT `fk_scenario_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `action` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `scenario_id` bigint NOT NULL,
  `symbol` varchar(15) NOT NULL COMMENT 'Stock symbol',
  `quantity` int NOT NULL COMMENT 'Number of shares',
  `activity` varchar(6) NOT NULL COMMENT 'Actions Buy or Sell',
  `price` decimal(16,2) NOT NULL COMMENT 'Price of the action: buy or sell',
  `commisions` decimal(6,2) DEFAULT NULL,
  `currency` varchar(3) DEFAULT NULL COMMENT 'Currency CAD, US, EUR',
  `activity_date` datetime DEFAULT NULL COMMENT 'When sold or bought',
  `created_on` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_action_scenario1_idx` (`scenario_id`),
  CONSTRAINT `fk_action_scenario1` FOREIGN KEY (`scenario_id`) REFERENCES `scenario` (`id`)
) ENGINE=InnoDB;


======================== NYSE ==========================================
CREATE TABLE `us_watch_symbol` (
  `symbol` varchar(10) NOT NULL COMMENT 'Stock symbol TSX with .TO',
  `quoterly_dividend_amount` decimal(10,4) DEFAULT NULL COMMENT 'Majority ov comapnies pay on quaterly basis',
  `upper_yield` decimal(6,4) DEFAULT NULL COMMENT 'Upper yeild where price is at lowest point',
  `lower_yield` decimal(6,4) DEFAULT NULL COMMENT 'Lowe yield when price is at highest point',
  `updated_on` datetime(6) DEFAULT NULL COMMENT 'Date when record was created or updated',
  PRIMARY KEY (`symbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

====== Volatility Dates ================================================
use golem;
create table volatility_date (
	`id` bigint NOT NULL AUTO_INCREMENT,
    `day_date` datetime DEFAULT CURRENT_TIMESTAMP,
    `action_description` VARCHAR(450) NULL,
    `description` VARCHAR(450) NULL,
    `active` INT NULL,  /*  0 - not active, 1 - active */
    `active_from_date` datetime NULL,
    `active_to_date` datetime NULL,
    `created_on` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_on` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) engine=InnoDB;

-- , UNIQUE KEY `UKvolatilitydate` (`day_date`)

CREATE TABLE `volatility_date_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
-------------------------------------------------------------------------






CREATE TABLE `watch_symbol` (
  `symbol` varchar(10) NOT NULL COMMENT 'Stock symbol TSX with .TO',
  `quoterly_dividend_amount` decimal(10,4) DEFAULT NULL COMMENT 'Majority ov comapnies pay on quaterly basis',
  `upper_yield` decimal(6,4) DEFAULT NULL COMMENT 'Upper yeild where price is at lowest point',
  `lower_yield` decimal(6,4) DEFAULT NULL COMMENT 'Lowe yield when price is at highest point',
  `updated_on` datetime(6) DEFAULT NULL COMMENT 'Date when record was created or updated',
  PRIMARY KEY (`symbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `symbol_status` (
  `symbol` varchar(10) NOT NULL COMMENT 'Stock ticker. TSX with .TO',
  `current_price` decimal(10,4) DEFAULT NULL COMMENT 'Cuurent price taken from yahoo finance',
  `quoterly_dividend_amount` decimal(10,4) DEFAULT NULL COMMENT 'Majority of comapnies pay dividends on a quaterly basis',
  `current_yield` decimal(6,4) DEFAULT NULL COMMENT 'Current yeild calculated from (quaterly_dividend_amount * 4) /current_price * 100',
  `upper_yield` decimal(6,4) DEFAULT NULL COMMENT 'Upper yield',
  `lower_yield` decimal(6,4) DEFAULT NULL COMMENT 'Lower_yield',
  `allowed_buy_yield` decimal(6,4) DEFAULT NULL,
  `sell_point_yield` decimal(6,4) DEFAULT NULL COMMENT 'Status of the findings: OUTDATED, NOT AVAILABLE, ERROR, ACTIVE',
  `allowed_buy_price` decimal(10,2) DEFAULT NULL COMMENT 'Price just below top yiled price. ',
  `best_buy_price` decimal(10,2) DEFAULT NULL COMMENT 'Best price to buy. It is at the top of the yield',
  `recommended_action` varchar(15) DEFAULT NULL COMMENT 'Possible Values: BUY, SELL, HOLD',
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

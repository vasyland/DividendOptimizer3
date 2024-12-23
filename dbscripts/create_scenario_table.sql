use golem;

CREATE TABLE `scenario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `scenario_name` varchar(45) NOT NULL COMMENT 'Some meaningfull description or name',
  `invested_amount` decimal(16,2) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `created_on` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'creation time',
  `updated_on` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_scenario_user_idx` (`user_id`),
  CONSTRAINT `fk_scenario_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

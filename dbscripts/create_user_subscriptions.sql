use golem;
drop table user_subscription;
drop table user_subscription_seq;

-- User Subscriptions
CREATE TABLE `user_subscription` (
  `id` int NOT NULL AUTO_INCREMENT,
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
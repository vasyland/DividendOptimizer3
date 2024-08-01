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
    PRIMARY KEY (`id`),
    UNIQUE KEY `UKvolatilitydate` (`day_date`)
) engine=InnoDB;


INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-02','666, Fr','(8+2+2)=>12=>(66)+(2+4)=>(666)',1,'2024-07-30','2024-08-08');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-01','26 of Tamus','Market on this day almost always down.',1,'2024-07-30','2024-08-08');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-05','1st of Av','9 day mornign period',1,'2024-07-30','2024-08-15');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-13','9 of Av','End of 9 day morning period',1,'2024-07-30','2024-08-15');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-14','Tisha BAv','End of 40day IGM blackout period',1,'2024-08-10','2024-08-20');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-19','Tu BAv','Minor Jewish holiday of love',1,'2024-08-15','2024-08-25');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-09-02','End of IGM 40 day blackuot period','29 of Av',1,'2024-08-15','2024-09-20');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-09-04','Rosha Hashana LaBehemot','1st of Elul',1,'2024-08-15','2024-09-25');




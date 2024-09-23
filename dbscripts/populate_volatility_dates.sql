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
VALUES ('2024-07-23','The Three Weeks',' Tuesday, July 23, 2024  through  Tuesday, August 13, 2024 ',1,'2024-07-10','2024-08-20');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-02','666, Fr','(8+2+2)=>12=>(66)+(2+4)=>(666)',1,'2024-07-30','2024-08-08');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-01','26 of Tamus','Market on this day almost always down.',1,'2024-07-30','2024-08-08');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-05','1st of Av','9 day mornign period',1,'2024-07-30','2024-08-15');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-12','9 of Av','End of 9 day morning period',1,'2024-07-30','2024-08-15');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-14','Tisha BAv','End of 40day IGM blackout period',1,'2024-08-10','2024-08-20');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-19','Tu B`Av - 15th of Av, Mon ','Minor Jewish holiday of love',1,'2024-08-15','2024-08-25');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-09-02','End of IGM 40 day blackuot period','29 of Av',1,'2024-08-15','2024-09-20');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-09-04','Rosha Hashana LaBehemot','1st of Elul',1,'2024-08-15','2024-09-25');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-05','66','=(8+5+2)=>15=>6+(24)=(66)  Manual Calculation',1,'2024-08-1','2024-08-15');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-08','6666','(8+8+2+2+4)=24=>(6666)',1,'2024-08-1','2024-08-15');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-11','18, THU  | 666 ','=(8+1+1+2)=12+6=(666) = 18',1,'2024-08-08','2024-08-15');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-20','18 => 666','Tuesday',1,'2024-08-10','2024-08-29');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-21','17 of Av - 67 Jews are killed in the Hebron Massacre','Wed',1,'2024-08-10','2024-08-29');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-23','21 => 666','Friday',1,'2024-08-18','2024-08-29');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-26','24 => 6666','Monday',1,'2024-08-18','2024-09-02');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-08-28','24 Av Wed','A Hasmonean holiday commemorates the reinstatement of Jewish civil law in place of Hellenist secular law on this day',1,'2024-08-18','2024-09-09');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-09-10','18 => 666','Tuesday',1,'2024-08-19','2024-09-20');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-09-16','24 => 6666','Monday',1,'2024-08-19','2024-09-30');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-09-25','24 => 6666','Wednesday',1,'2024-08-19','2024-10-05');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-10-03','12 => 66','Thusday',1,'2024-08-19','2024-10-25');




INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-10-02','Rosh Hashanah, Wed ','Begins sunset of  Wednesday, October 2, 2024; Ends nightfall of  Friday, October 4, 2024 ',1,'2024-09-08','2024-10-15');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-10-11','Yom Kippur','sunset of  Friday, October 11, 2024; nightfall of  Saturday, October 12, 2024',1,'2024-09-08','2024-10-25');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-10-16','Sukkot','sunset of  Wednesday, October 16, 2024; nightfall of  Wednesday, October 23, 2024',1,'2024-10-05','2024-10-28');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-11-05','US Presidential Election','Tuesday',1,'2024-11-01','2024-11-10');

INSERT INTO `volatility_date` ( `day_date`, `action_description`, `description`, `active`, `active_from_date`, `active_to_date`) 
VALUES ('2024-11-05','US Presidential Election','Tuesday',1,'2024-11-01','2024-11-10');



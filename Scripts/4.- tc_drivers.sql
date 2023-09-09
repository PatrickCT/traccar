-- Active: 1688747211561@@127.0.0.1@3306@tc_traccer
-- CREATE TABLE tc_drivers (
--    driverID INTEGER PRIMARY KEY AUTO_INCREMENT,
--    name VARCHAR(75),
--    uniqueId VARCHAR(50)
--);
ALTER TABLE tc_drivers ADD COLUMN phone VARCHAR(50);
ALTER TABLE tc_drivers ADD COLUMN license VARCHAR(50);
ALTER TABLE tc_drivers ADD COLUMN age INT;
-- DESC tc_drivers;
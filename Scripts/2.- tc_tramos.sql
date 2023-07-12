-- Active: 1688747211561@@127.0.0.1@3306@tc_traccer
CREATE TABLE tc_tramos (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50),
    minTime INT, maxTime INT, geofenceId INT, punishment INT);
-- DESCRIBE tc_tramos;
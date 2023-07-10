-- Active: 1688747211561@@127.0.0.1@3306@tc_traccer
CREATE TABLE tc_salidas (
    salidaID INTEGER PRIMARY KEY AUTO_INCREMENT,
    finished BOOLEAN,
    date DATE,
    deviceId INT,
    scheduleId INT
);
--DESC tc_salidas;
-- Active: 1688747211561@@127.0.0.1@3306@tc_traccer
CREATE TABLE tc_tickets (ticketID INT PRIMARY KEY AUTO_INCREMENT,salidaId INT, geofenceId INT, expectedTime DATE, realTime DATE, 
difference DOUBLE, punishment INT);
-- DESCRIBE tc_tickets;

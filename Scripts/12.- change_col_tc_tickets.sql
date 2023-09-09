-- Active: 1688747211561@@127.0.0.1@3306@gpstracker5
ALTER TABLE `tc_tickets`   
	DROP COLUMN `realTime`;
ALTER TABLE `tc_tickets`   
	ADD COLUMN `enterTime` DATETIME NULL AFTER `expectedTime`;

ALTER TABLE `tc_tickets`   
	ADD COLUMN `exitTime` DATETIME NULL AFTER `enterTime`;   
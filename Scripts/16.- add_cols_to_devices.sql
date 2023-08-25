ALTER TABLE `tc_devices`   
	ADD COLUMN `carPlate` VARCHAR(100) NULL AFTER `motionstreak`,
	ADD COLUMN `serie` VARCHAR(100) NULL AFTER `carPlate`,
	ADD COLUMN `year` VARCHAR(4) NULL AFTER `serie`,
	ADD COLUMN `maker` VARCHAR(100) NULL AFTER `year`,
	ADD COLUMN `policy` VARCHAR(200) NULL AFTER `maker`,
	ADD COLUMN `insuranceExpiration` DATETIME NULL AFTER `policy`;
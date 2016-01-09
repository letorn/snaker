DELIMITER $$

DROP PROCEDURE IF EXISTS `save_or_update_import_resume`$$

CREATE DEFINER=`admin`@`%` PROCEDURE `save_or_update_import_resume`(
    IN userID VARCHAR (255),
    IN NAME VARCHAR (255),
    IN gender VARCHAR (255),
    IN birthDate VARCHAR (255),
    IN mobile VARCHAR (255),
    IN email VARCHAR (255),
    IN serviceYear VARCHAR (255),
    IN location VARCHAR (255),
    IN locationCode VARCHAR (255),
    IN jobStatus VARCHAR (255),
    IN householder VARCHAR (255),
    IN credentials VARCHAR (255),
    IN pcred VARCHAR (255),
    IN address VARCHAR (255),
    IN lbsLon VARCHAR (255),
    IN lbsLat VARCHAR (255),
    IN createDate VARCHAR (255),
    IN isMarried VARCHAR (255),
    OUT msg VARCHAR (255),
    OUT isSuccess INTEGER (11)
)
BEGIN
    DECLARE statusID BIGINT (20);
    SET createDate = IF(
        createDate IS NULL
        OR createDate = '',
        NOW(),
        createDate
    );
    SET msg = '';
    SET isSuccess = 1;
    IF userID IS NULL
    THEN 
        SET msg = '用户不存在！';
        SET isSuccess = 0;
    ELSE
        UPDATE `zcdh_jobhunte_user` u
        SET 
            `name` = NAME,
            `pgender` = gender,
            `birth` = IF(birthDate IS NULL OR birthDate = '', u.birth, birthDate),
            `mobile` = IF(mobile IS NULL OR mobile = '', u.mobile, mobile),
            `email` = IF(email IS NULL OR email = '', u.email, email),
            `pservice_year` = IF(serviceYear IS NULL OR serviceYear = '', u.pservice_year, serviceYear),
            `paddress` = IF(locationCode IS NULL OR locationCode = '', u.paddress, locationCode),
            `panmelden` = IF(householder IS NULL OR householder = '', u.panmelden, householder),
            `credentials` = IF(credentials IS NULL OR credentials = '', u.credentials, credentials),
            `pcred` = IF(pcred IS NULL OR pcred = '', u.pcred, pcred),
            `position` = IF(address IS NULL OR address = '', u.position, address),
            `city` = IF(location IS NULL OR location = '', u.city, location),
            `lat` = IF(lbsLat IS NULL OR lbsLat = '', u.lat, lbsLat),
            `lon` = IF(lbsLon IS NULL OR lbsLon = '', u.lon, lbsLon),
            `create_date` = createDate,
            `pis_married` = IF(isMarried IS NULL OR isMarried = '', u.pis_married, isMarried)
        WHERE `user_id` = userID;
        SET statusID = 0;
        SELECT id INTO statusID FROM zcdh_jobhunte_user_status WHERE user_id = userID LIMIT 1;
        IF statusID = 0
        THEN 
            INSERT INTO `zcdh_jobhunte_user_status`(
                `code`,
                `user_id`
            ) VALUES (
                IF(jobStatus IS NULL OR jobStatus = '', NULL, jobStatus),
                userID
            );
        ELSE
            UPDATE `zcdh_jobhunte_user_status` s
            SET
                `code` = IF(jobStatus IS NULL OR jobStatus = '', s.code, jobStatus)
            WHERE id = statusID;
        END IF;
    END IF;
    
END$$

DELIMITER ;
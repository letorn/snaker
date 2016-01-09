DELIMITER $$

DROP PROCEDURE IF EXISTS `save_or_update_prize`$$

CREATE DEFINER=`admin`@`%` PROCEDURE `save_or_update_prize`(
    IN userID VARCHAR (255),
    IN prizeTime VARCHAR (255),
    IN prizeName VARCHAR (255),
    IN prizeContent VARCHAR (255),
    OUT msg VARCHAR (255),
    OUT isSuccess INTEGER (11)
)
BEGIN
    DECLARE prizeID BIGINT (20);
    SET prizeID = 0;
    SET msg = '';
    SET isSuccess = 1;
    IF userID IS NULL
    THEN 
        SET msg = '用户不存在！';
        SET isSuccess = 0;
    ELSE
        SELECT prizes_id INTO prizeID FROM zcdh_jobhunte_prizes WHERE user_id = userID AND prizes_name = prizeName AND TIME = prizeTime LIMIT 1;
        IF prizeID = 0
        THEN
            INSERT INTO `zcdh_jobhunte_prizes`(
                `prizes_description`,
                `time`,
                `prizes_name`,
                `user_id`
            ) VALUES (
                prizeContent,
                prizeTime,
                prizeName,
                userID
            );
        ELSE
            UPDATE `zcdh_jobhunte_prizes` p
            SET
                `prizes_description` = IF(prizeContent IS NULL OR prizeContent = '', p.prizes_description, prizeContent)
            WHERE `prizes_id` = prizeID;
        END IF;
    END IF;
END$$

DELIMITER ;
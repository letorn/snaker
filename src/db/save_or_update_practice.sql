DELIMITER $$

DROP PROCEDURE IF EXISTS `save_or_update_practice`$$

CREATE DEFINER=`admin`@`%` PROCEDURE `save_or_update_practice`(
    IN userID VARCHAR (255),
    IN startTime VARCHAR (255),
    IN endTime VARCHAR (255),
    IN practiceName VARCHAR (255),
    IN practiceContent VARCHAR (255),
    OUT msg VARCHAR (255),
    OUT isSuccess INTEGER (11)
)
BEGIN
    DECLARE practiceID BIGINT (20);
    SET practiceID = 0;
    SET msg = '';
    SET isSuccess = 1;
    IF userID IS NULL
    THEN 
        SET msg = '用户不存在！';
        SET isSuccess = 0;
    ELSE
        SELECT practice_id INTO practiceID FROM zcdh_jobhunte_practice WHERE user_id = userID AND practice_name = practiceName LIMIT 1;
        IF practiceID = 0
        THEN
            INSERT INTO `zcdh_jobhunte_practice`(
                `end_time`,
                `practice_description`,
                `practice_name`,
                `start_time`,
                `user_id`
            ) VALUES (
                endTime,
                practiceContent,
                practiceName,
                startTime,
                userID
            );
        ELSE
            UPDATE `zcdh_jobhunte_practice` p
            SET 
                `end_time` = IF(endTime IS NULL OR endTime = '', p.end_time, endTime),
                `start_time` = IF(startTime IS NULL OR startTime = '', p.start_time, startTime),
                `practice_description` = IF(practiceContent IS NULL OR practiceContent = '', p.practice_description, practiceContent)
            WHERE `practice_id` = practiceID;
        END IF;
    END IF;
END$$

DELIMITER ;
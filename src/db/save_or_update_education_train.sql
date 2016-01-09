DELIMITER $$

DROP PROCEDURE IF EXISTS `save_or_update_education_train`$$

CREATE DEFINER=`admin`@`%` PROCEDURE `save_or_update_education_train`(
    IN userID VARCHAR (255),
    IN startTime VARCHAR (255),
    IN endTime VARCHAR (255),
    IN institution VARCHAR (255),
    IN course VARCHAR (255),
    IN certificate VARCHAR (255),
    IN content VARCHAR (255),
    OUT msg VARCHAR (255),
    OUT isSuccess INTEGER (11)
)
BEGIN
    DECLARE trainID BIGINT (20);
    SET msg = '';
    SET isSuccess = 1;
    SET trainID = 0;
    
    IF userID IS NULL
    THEN 
        SET msg = '用户不存在！';
        SET isSuccess = 0;
    ELSE
        SELECT t.id INTO trainID FROM zcdh_jobhunte_education_train t WHERE t.user_id = userID AND t.institution = institution AND t.course_code = course LIMIT 1;
        IF trainID = 0
        THEN
            INSERT INTO `zcdh_jobhunte_education_train`(
                `content`,
                `course_code`,
                `cre_code`,
                `end_time`,
                `institution`,
                `start_time`,
                `user_id`
            ) VALUES (
                content,
                course,
                certificate,
                endTime,
                institution,
                startTime,
                userID
            );
        ELSE
            UPDATE `zcdh_jobhunte_education_train`
            SET 
                `content` = content,
                `course_code` = course,
                `cre_code` = certificate,
                `end_time` = endTime,
                `institution` = institution,
                `start_time` = startTime
            WHERE `id` = trainID;
        END IF;
    END IF;
    
END$$

DELIMITER ;
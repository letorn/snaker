DELIMITER $$

DROP PROCEDURE IF EXISTS `save_or_update_education`$$

CREATE DEFINER=`admin`@`%` PROCEDURE `save_or_update_education`(
    IN userID VARCHAR (255),
    IN startTime VARCHAR (255),
    IN endTime VARCHAR (255),
    IN school VARCHAR (255),
    IN schoolCode VARCHAR(255),
    IN major VARCHAR (255),
    IN degree VARCHAR (255),
    IN content VARCHAR (1000),
    IN createDate VARCHAR (255),
    IN eduType VARCHAR (255),
    OUT msg VARCHAR (255),
    OUT isSuccess INTEGER (11)
)
BEGIN
    DECLARE eduID BIGINT (20);
    SET content = IF(
        content IS NULL
        OR content = '',
        NULL,
        content
    );
    SET createDate = IF(
        createDate IS NULL
        OR createDate = '',
        NOW(),
        createDate
    );
    SET schoolCode = IF(
     schoolCode IS NULL
     OR schoolCode = '',
     'UserWriteSelf',
     schoolCode
    );
    SET eduType = IF(
        eduType IS NULL
        OR eduType = '',
        NULL,
        eduType
    );
    SET msg = '';
    SET isSuccess = 1;
    
    IF userID IS NULL
    THEN 
        SET msg = '用户不存在！';
        SET isSuccess = 0;
    ELSE
        SET eduID = 0;
        SELECT edu_id INTO eduID FROM `zcdh_jobhunte_education` WHERE `user_id` = userID AND `school` = school AND `pdegree` = degree LIMIT 1;
        IF eduID = 0
        THEN 
            INSERT INTO `zcdh_jobhunte_education` (
                `user_id`,
                `school`,
                `school_code`,
                `start_time`,
                `end_time`,
                `pmajor`,
                `pdegree`,
                `edu_type`,
                `create_time`,
                `content`
            ) VALUES (
                userID,
                school,
                schoolCode,
                startTime,
                endTime,
                major,
                degree,
                eduType,
                createDate,
                content
            );
        ELSE 
            UPDATE `zcdh_jobhunte_education` e
            SET 
                `school` = school,
                `school_code` = schoolCode,
                `start_time` = startTime,
                `end_time` = endTime,
                `pmajor` = major,
                `pdegree` = degree,
                `edu_type` = eduType,
                `create_time` = createDate,
                `content` = content
            WHERE `edu_id` = eduID;
        END IF;
        IF degree IS NOT NULL
        THEN
            UPDATE `zcdh_jobhunte_user`
            SET 
                `peducation` = degree
            WHERE `user_id` = userID;
        END IF;
    END IF;
END$$

DELIMITER ;
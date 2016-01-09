DELIMITER $$

DROP PROCEDURE IF EXISTS `save_or_update_wep_post`$$

CREATE DEFINER=`admin`@`%` PROCEDURE `save_or_update_wep_post`(
    IN userID VARCHAR (255),
    IN company VARCHAR (255),
    IN startTime VARCHAR (255),
    IN endTime VARCHAR (255),
    IN industry VARCHAR (255),
    IN postCode VARCHAR (255),
    IN createTime VARCHAR (255),
    IN workContent VARCHAR (1000),
    OUT msg VARCHAR (255),
    OUT isSuccess INTEGER (11)
)
BEGIN
    DECLARE weppostID BIGINT (20);
    SET createTime = IF(
        createTime IS NULL
        OR createTime = '',
        NOW(),
        createTime
    );
    SET workContent = IF(
        workContent IS NULL
        OR workContent = '',
        NULL,
        workContent
    );
    SET msg = '';
    SET isSuccess = 1;
    IF userID IS NULL
    THEN 
        SET msg = '用户不存在！';
        SET isSuccess = 0;
    ELSE
        SET weppostID = 0;
        SELECT weppost_id INTO weppostID FROM `zcdh_jobhunte_wep_post` WHERE `post_code` = postCode AND `cor_name` = company AND `user_id` = userID LIMIT 1;
        IF weppostID = 0
        THEN
            INSERT INTO `zcdh_jobhunte_wep_post`(
	        `create_time`,
	        `post_code`,
	        `user_id`,
	        `work_content`,
	        `cor_name`,
	        `end_time`,
	        `start_time`,
	        `industry_code`
            ) VALUES (
                createTime,
                postCode,
                userID,
                workContent,
                company,
                endTime,
                startTime,
                industry
            );
        ELSE
            UPDATE `zcdh_jobhunte_wep_post`
            SET
                `create_time` = createTime,
                `post_code` = postCode,
                `work_content` = workContent,
                `cor_name` = company,
                `end_time` = endTime,
                `start_time` = startTime,
                `industry_code` = industry
            WHERE `weppost_id` = weppostID;
        END IF;
    END IF;
    
END$$

DELIMITER ;
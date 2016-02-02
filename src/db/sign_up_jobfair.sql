DELIMITER $$

DROP PROCEDURE IF EXISTS `sign_up_jobfair`$$

CREATE DEFINER=`admin`@`%` PROCEDURE `sign_up_jobfair`(
    IN fairId VARCHAR (255),
    IN boothNo VARCHAR (255),
    IN dataSrc VARCHAR (255),
    IN entName VARCHAR (255),
    OUT msg VARCHAR (255),
    OUT isSuccess INTEGER (11)
)
BEGIN
    DECLARE eid BIGINT (20);
    SET eid = 0;
    SET msg = '成功！';
    SET isSuccess = 1;
    
    SET boothNo = IF(
        boothNo IS NULL 
        OR boothNo = ''
        OR LENGTH(boothNo) = 0,
        '待分配',
        boothNo
    );
    
    IF fairId IS NULL OR fairId = '' OR LENGTH(fairId) = 0
    THEN
        SET msg = '招聘会ID错误！';
        SET isSuccess = 0;    
    ELSE
        SELECT ent_id INTO eid FROM zcdh_ent_enterprise WHERE data_src = dataSrc AND data_key = entName;
        IF eid IN (SELECT ent_id FROM zcdh_jobfair_ent WHERE fair_id = fairId)
        THEN
            SET msg = '企业已报名此招聘会';
            SET isSuccess = 0;
        ELSE
            INSERT INTO zcdh_jobfair_ent(create_time, fair_id, ent_id ,is_sign_up, audit_status, booth_no)
                VALUES (NOW(), fairId, eid, 2,2, boothNo);
        END IF;
        INSERT INTO zcdh_jobfair_ent_post(create_time, fair_id , post_id, ent_id)
                SELECT NOW(), fairId, id, eid FROM zcdh_ent_post WHERE data_src = dataSrc AND data_ent_key = entName AND id NOT IN (SELECT post_id FROM zcdh_jobfair_ent_post WHERE fair_id = fairId AND ent_id = eid);
    END IF;
END$$

DELIMITER ;
DELIMITER $$

DROP PROCEDURE IF EXISTS `save_or_update_objective`$$

CREATE DEFINER=`admin`@`%` PROCEDURE `save_or_update_objective`(
    IN userID VARCHAR (255),
    IN selfComment VARCHAR (1000),
    IN location1 VARCHAR (255),
    IN location2 VARCHAR (255),
    IN location3 VARCHAR (255),
    IN industry1 VARCHAR (255),
    IN industry2 VARCHAR (255),
    IN industry3 VARCHAR (255),
    IN post1 VARCHAR (255),
    IN post2 VARCHAR (255),
    IN post3 VARCHAR (255),
    IN jobterm VARCHAR (255),
    IN minSalary VARCHAR (255),
    IN maxSalary VARCHAR (255),
    IN createDate VARCHAR (255),
    OUT msg VARCHAR (255),
    OUT isSuccess INTEGER (11)
)
BEGIN
    DECLARE objectiveID BIGINT (20);
    DECLARE areaID BIGINT (20);
    DECLARE industryID BIGINT (20);
    DECLARE postID BIGINT (20);
    DECLARE negotiable VARCHAR (255);
    SET createDate = IF(
        createDate IS NULL
        OR createDate = '',
        NOW(),
        createDate
    );
    SET negotiable = IF(
        minSalary IS NULL OR minSalary = '',
        'true',
        'false'
    );
    SET minSalary = IF(
        minSalary IS NULL OR minSalary = '',
        NULL,
        minSalary
    );
    SET maxSalary = IF(
        maxSalary IS NULL OR maxSalary = '',
        NULL,
        maxSalary
    );
    SET msg = '';
    SET isSuccess = 1;
    SET objectiveID = 0;
    SET areaID = 0;
    SET industryID = 0;
    SET postID = 0;
    IF userID IS NOT NULL
    THEN 
        UPDATE `zcdh_jobhunte_user` u
        SET 
            `self_comment` = IF(selfComment IS NULL OR selfComment = '', u.self_comment, selfComment)
        WHERE user_id = userID;
        
        SELECT id INTO objectiveID FROM `zcdh_jobhunte_objective` WHERE `user_id` = userID LIMIT 1;
        IF objectiveID = 0
        THEN 
            INSERT INTO `zcdh_jobhunte_objective`(
                `user_id`,
                `pobj_status`,
                `min_salary`,
                `max_salary`,
                `negotiableSalary`,
                `create_date`
            ) VALUES (
                userID,
                jobterm,
                minSalary,
                maxSalary,
                negotiable,
                createDate
            );
        ELSE
            UPDATE `zcdh_jobhunte_objective` o
            SET 
                `pobj_status` = IF(jobterm IS NULL OR jobterm = '', o.pobj_status, jobterm),
                `min_salary` = minSalary,
                `max_salary` = maxSalary,
                `negotiableSalary` = negotiable,
                `create_date` = createDate
            WHERE `id` = objectiveID;
        END IF;
        
        IF location1 IS NOT NULL AND LENGTH(location1) != 0
        THEN 
            SELECT id INTO areaID FROM zcdh_jobhunte_objective_area WHERE user_id = userID LIMIT 1;
            IF areaID = 0
            THEN 
                INSERT INTO `zcdh_jobhunte_objective_area` (
                    `area_code`,
                    `user_id`,
                    `create_Date`
                ) VALUES (
                    location1,
                    userID,
                    createDate
                );
            ELSE
                UPDATE `zcdh_jobhunte_objective_area`
                SET 
                    `area_code` = location1,
                    `create_Date` = createDate
                WHERE `id` = areaID;
            END IF;
            SET areaID = 0;
        END IF;
        
        IF location2 IS NOT NULL AND LENGTH(location2) != 0
        THEN 
            SELECT id INTO areaID FROM zcdh_jobhunte_objective_area WHERE user_id = userID LIMIT 1, 1;
            IF areaID = 0
            THEN 
                INSERT INTO `zcdh_jobhunte_objective_area` (
                    `area_code`,
                    `user_id`,
                    `create_Date`
                ) VALUES (
                    location2,
                    userID,
                    createDate
                );
            ELSE
                UPDATE `zcdh_jobhunte_objective_area`
                SET 
                    `area_code` = location2,
                    `create_Date` = createDate
                WHERE `id` = areaID;
            END IF;
            SET areaID = 0;
        END IF;
        
        IF location3 IS NOT NULL AND LENGTH(location3) != 0
        THEN 
            SELECT id INTO areaID FROM zcdh_jobhunte_objective_area WHERE user_id = userID LIMIT 2, 1;
            IF areaID = 0
            THEN 
                INSERT INTO `zcdh_jobhunte_objective_area` (
                    `area_code`,
                    `user_id`,
                    `create_Date`
                ) VALUES (
                    location3,
                    userID,
                    createDate
                );
            ELSE
                UPDATE `zcdh_jobhunte_objective_area`
                SET 
                    `area_code` = location3,
                    `create_Date` = createDate
                WHERE `id` = areaID;
            END IF;
        END IF;
        
        IF industry1 IS NOT NULL AND LENGTH(industry1) != 0
        THEN
            SELECT id INTO industryID FROM zcdh_jobhunte_objective_industry WHERE user_id = userID LIMIT 1;
            IF industryID = 0
            THEN
                INSERT INTO `zcdh_jobhunte_objective_industry`(
                    `code`,
                    `create_time`,
                    `user_id`
                ) VALUES (
                    industry1,
                    createDate,
                    userID
                );
            ELSE
                UPDATE `zcdh_jobhunte_objective_industry`
                SET
                    `code` = industry1,
                    `create_time` = createDate
                WHERE `id` = industryID;
            END IF;
            SET industryID = 0;
        END IF;
        
        IF industry2 IS NOT NULL AND LENGTH(industry2) != 0
        THEN
            SELECT id INTO industryID FROM zcdh_jobhunte_objective_industry WHERE user_id = userID LIMIT 1, 1;
            IF industryID = 0
            THEN
                INSERT INTO `zcdh_jobhunte_objective_industry`(
                    `code`,
                    `create_time`,
                    `user_id`
                ) VALUES (
                    industry2,
                    createDate,
                    userID
                );
            ELSE
                UPDATE `zcdh_jobhunte_objective_industry`
                SET
                    `code` = industry2,
                    `create_time` = createDate
                WHERE `id` = industryID;
            END IF;
            SET industryID = 0;
        END IF;
        
        IF industry3 IS NOT NULL AND LENGTH(industry3) != 0
        THEN
            SELECT id INTO industryID FROM zcdh_jobhunte_objective_industry WHERE user_id = userID LIMIT 2, 1;
            IF industryID = 0
            THEN
                INSERT INTO `zcdh_jobhunte_objective_industry`(
                    `code`,
                    `create_time`,
                    `user_id`
                ) VALUES (
                    industry3,
                    createDate,
                    userID
                );
            ELSE
                UPDATE `zcdh_jobhunte_objective_industry`
                SET
                    `code` = industry3,
                    `create_time` = createDate
                WHERE `id` = industryID;
            END IF;
        END IF;
        
        IF post1 IS NOT NULL AND LENGTH(post1) != 0
        THEN
            SELECT ojp_id INTO postID FROM zcdh_jobhunte_objective_post WHERE user_id = userID LIMIT 1;
            IF postID = 0
            THEN
                INSERT INTO `zcdh_jobhunte_objective_post`(
                    `code`,
                    `create_time`,
                    `user_id`
                ) VALUES (
                    post1,
                    createDate,
                    userID
                );
            ELSE
                UPDATE `zcdh_jobhunte_objective_post`
                SET
                    `code` = post1,
                    `create_time` = createDate
                WHERE `ojp_id` = postID;
            END IF;
            SET postID = 0;
        END IF;
        
        IF post2 IS NOT NULL AND LENGTH(post2) != 0
        THEN
            SELECT ojp_id INTO postID FROM zcdh_jobhunte_objective_post WHERE user_id = userID LIMIT 1, 1;
            IF postID = 0
            THEN
                INSERT INTO `zcdh_jobhunte_objective_post`(
                    `code`,
                    `create_time`,
                    `user_id`
                ) VALUES (
                    post2,
                    createDate,
                    userID
                );
            ELSE
                UPDATE `zcdh_jobhunte_objective_post`
                SET
                    `code` = post2,
                    `create_time` = createDate
                WHERE `ojp_id` = postID;
            END IF;
            SET postID = 0;
        END IF;
        
        IF post3 IS NOT NULL AND LENGTH(post3) != 0
        THEN
            SELECT ojp_id INTO postID FROM zcdh_jobhunte_objective_post WHERE user_id = userID LIMIT 2, 1;
            IF postID = 0
            THEN
                INSERT INTO `zcdh_jobhunte_objective_post`(
                    `code`,
                    `create_time`,
                    `user_id`
                ) VALUES (
                    post3,
                    createDate,
                    userID
                );
            ELSE
                UPDATE `zcdh_jobhunte_objective_post`
                SET
                    `code` = post3,
                    `create_time` = createDate
                WHERE `ojp_id` = postID;
            END IF;
        END IF;
    ELSE
        SET msg = '用户不存在！';
        SET isSuccess = 0;
    END IF;
    
END$$

DELIMITER ;
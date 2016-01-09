DELIMITER $$

DROP PROCEDURE IF EXISTS `save_or_update_ability`$$

CREATE DEFINER=`admin`@`%` PROCEDURE `save_or_update_ability`(
    IN userID VARCHAR (255),
    IN technologyName VARCHAR (255),
    IN technologyCode VARCHAR (255),
    IN technologyAbility VARCHAR (255),
    OUT msg VARCHAR (255),
    OUT isSuccess INTEGER (11)
)
BEGIN
    DECLARE abilityID BIGINT (20);
    SET abilityID = 0;
    SET msg = '';
    SET isSuccess = 1;
    IF userID IS NULL
    THEN 
        SET msg = '用户不存在！';
        SET isSuccess = 0;
    ELSE
        SELECT id INTO abilityID FROM zcdh_jobhunte_ability WHERE user_id = userID AND technology_code = technologyCode LIMIT 1;
        IF abilityID = 0
        THEN
            INSERT INTO `zcdh_jobhunte_ability`(
                `param_code`,
                `technology_code`,
                `technology_name`,
                `user_id`
            ) VALUES (
                technologyAbility,
                technologyCode,
                technologyName,
                userID
            );
        ELSE
            UPDATE `zcdh_jobhunte_ability` a
            SET
                `param_code` = IF(technologyAbility IS NULL OR technologyAbility = '', a.param_code, technologyAbility),
                `technology_name` = IF(technologyName IS NULL OR technologyName = '', a.technology_name, technologyName)
            WHERE `id` = abilityID;
        END IF;
    END IF;
END$$

DELIMITER ;
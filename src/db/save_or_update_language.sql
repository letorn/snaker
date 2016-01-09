DELIMITER $$

DROP PROCEDURE IF EXISTS `save_or_update_language`$$

CREATE DEFINER=`admin`@`%` PROCEDURE `save_or_update_language`(
    IN userID VARCHAR (255),
    IN languageName VARCHAR (255),
    IN readWrite VARCHAR (255),
    IN listenSpeak VARCHAR (255),
    OUT msg VARCHAR (255),
    OUT isSuccess INTEGER (11)
)
BEGIN
    DECLARE languageID BIGINT (20);
    SET languageID = 0;
    SET msg = '';
    SET isSuccess = 1;
    IF userID IS NULL
    THEN 
        SET msg = '用户不存在！';
        SET isSuccess = 0;
    ELSE
        SELECT id INTO languageID FROM zcdh_jobhunte_language WHERE user_id = userID AND language_code = languageName LIMIT 1;
        IF languageID = 0
        THEN
            INSERT INTO `zcdh_jobhunte_language`(
                `hear_speack_param_code`,
                `language_code`,
                `read_write_param_code`,
                `user_id`
            ) VALUES (
                listenSpeak,
                languageName,
                readWrite,
                userID
            );
        ELSE
            UPDATE `zcdh_jobhunte_language`
            SET 
		`hear_speack_param_code` = listenSpeak,
		`read_write_param_code` = readWrite
            WHERE `id` = languageID;
        END IF;
    END IF;
END$$

DELIMITER ;
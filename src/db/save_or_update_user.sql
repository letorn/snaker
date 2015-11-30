delimiter $$

drop procedure if exists `save_or_update_user`$$

create procedure `save_or_update_user`(
    in name varchar (255),
    in gender varchar (255),
    in nation varchar (255),
    in mobile varchar (255),
    in email varchar (255),
    in experiencecode varchar (255),
    in educationcode varchar (255),
    in household varchar (255),
    in categorycode varchar (255),
    in status varchar (255),
    in marriage varchar (255),
    in certtype varchar (255),
    in certid varchar (255),
    in birth varchar (255),
    in selfcomment varchar (255),
    in locationcode varchar (255),
    in datasrc varchar (255),
    in datakey varchar (255),
    in updatedate varchar (255),
    in createdate varchar (255),
    in de_account varchar (255),
    in current varchar (255),
    in currentphone varchar (255),
    in currpostcode varchar (255),
    in currpost varchar (255),
    in lbslon varchar (255),
    in lbslat varchar (255),
    in accountstatus varchar (255),
    out msg varchar (255),
    out issuccess integer (11)
)
begin
    declare uid bigint (20) ;
    declare status_id bigint (20) ;
    declare num1 integer (11) ;
    declare num2 integer (11) ;
    declare num3 integer (11) ;
    declare accountid bigint (20) ;
    set updatedate = if(
        updatedate is null 
        or updatedate = '',
        now(),
        updatedate
    ) ;
    set createdate = if(
        createdate is null 
        or createdate = '',
        now(),
        createdate
    ) ;
    set birth = if(
        birth is null 
        or birth = '',
        null,
        birth
    ) ;
    set lbslon = if(
        lbslon is null 
        or lbslon = '',
        null,
        lbslon
    ) ;
    set lbslat = if(
        lbslat is null 
        or lbslat = '',
        null,
        lbslat
    ) ;
    set uid = 0 ;
    set status_id = 0 ;
    set num1 = 0 ;
    set num2 = 0 ;
    set num3 = 0 ;
    set msg = '' ;
    set issuccess = 1 ;
     select 
        user_id into uid 
    from
        zcdh_jobhunte_user 
    where data_key = datakey 
        and data_src = datasrc 
    limit 1 ;
    
    
    if name is null 
    or name = '' 
    then set msg = '姓名不能为空' ;
    set issuccess = 0 ;
    end if ;
    
    if gender = 1 
    then set gender = "003.001" ;
    elseif gender = 0 
    then set gender = '003.002' ;
    else set gender = '003.003' ;
    end if ;
    if uid = 0 
    then 
    select 
        account_id into accountid 
    from
        zcdh_jobhunte_account a 
    where a.account = de_account  limit 1;
    if accountid != 0 
    then set msg = '账号已存在' ;
    set issuccess = 0 ;
    end if ;
    end if ;
    if uid = 0 
    then 
    insert zcdh_jobhunte_user (
        birth,
        talent_type,
        credentials,
        email,
        mobile,
        name,
        nation,
        paddress,
        panmelden,
        pcred,
        peducation,
        pgender,
        pis_married,
        pservice_year,
        self_comment,
        create_date,
        lat,
        lon,
        update_date,
        data_key,
        data_src,
        curentname,
        curpost,
        cur_post_code,
        curentmobile
    ) 
    values
        (
            birth,
            categorycode,
            certid,
            email,
            mobile,
            name,
            nation,
            locationcode,
            household,
            certtype,
            educationcode,
            gender,
            marriage,
            experiencecode,
            selfcomment,
            createdate,
            lbslat,
            lbslon,
            updatedate,
            datakey,
            datasrc,
            current,
            currpost,
            currpostcode,
            currentphone
        ) ;
    select 
        @@identity into uid ;
    insert into `zcdh_jobhunte_account` (
        `account`,
        `create_time`,
        `status`,
        `user_id`
    ) 
    values
        (de_account, now(), accountstatus, uid) ;
    select 
        count(1) into num1 
    from
        zcdh_jobhunte_bind_user_account 
    where bind_value = de_account and bind_type= datasrc
    limit 1 ;
    if num1 = 0 
    then 
    insert into `zcdh_jobhunte_bind_user_account` (
        `bind_time`,
        `bind_type`,
        `bind_value`,
        `user_id`
    ) 
    values
        (now(), datasrc, de_account, uid) ;
    end if ;
    select 
        count(1) into num2 
    from
        zcdh_jobhunte_quick_login_account 
    where warranty_id = de_account and type= datasrc
    limit 1 ;
    if num2 = 0 
    then 
    insert into `zcdh_jobhunte_quick_login_account` (
        `create_time`,
        `type`,
        `user_id`,
        `warranty_id`
    ) 
    values
        (now(), datasrc, uid, de_account) ;
    end if ;
    select 
        count(1) into num3 
    from
        zcdh_admin_appconfig_role_user 
    where user_id = uid 
    limit 1 ;
    if num3 = 0 
    then 
    insert into `zcdh_admin_appconfig_role_user` (
        `create_time`,
        `memo`,
        `role_id`,
        `user_id`
    ) 
    values
        (now(), '缺省角色', 1, uid) ;
    end if ;
    select 
        id into status_id 
    from
        zcdh_jobhunte_user_status 
    where user_id = uid 
    limit 1 ;
    if status_id = 0 
    then 
    insert into `zcdh_jobhunte_user_status` (`code`, `user_id`) 
    values
        (status, uid) ;
    else 
    update 
        `zcdh_jobhunte_user_status` 
    set
        `code` = status 
    where `user_id` = uid ;
    end if ;
    else 
    update 
        `zcdh_jobhunte_user` 
    set
        `birth` = birth,
        `talent_type` = categorycode,
        `credentials` = certid,
        `email` = email,
        `mobile` = mobile,
        `name` = name,
        `nation` = nation,
        `paddress` = locationcode,
        `panmelden` = household,
        `pcred` = certtype,
        `peducation` = educationcode,
        `pgender` = gender,
        `pis_married` = marriage,
        `pservice_year` = experiencecode,
        `self_comment` = selfcomment,
        `create_date` = createdate,
        `lat` = lbslat,
        `lon` = lbslon,
        `update_date` = updatedate,
        `data_key` = datakey,
        `data_src` = datasrc,
        `curentname` = current,
        `curpost` = currpost,
        `cur_post_code` = currpostcode,
        `curentmobile` = currentphone 
    where `user_id` = uid ;
    end if ;
end$$

delimiter ;
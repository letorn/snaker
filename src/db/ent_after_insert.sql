delimiter $$

drop trigger `ent_after_insert`$$

create
    trigger `ent_after_insert` after insert on `db_enterprise` 
    for each row begin
    declare de_id bigint ;
    set de_id = 0 ;
    select 
        id into de_id 
    from
        vi_enterprise 
    where name = new.name ;
    if de_id != 0 
    then 
    update 
        `vi_enterprise` 
    set
        `contacter` = if(
            contacter is null 
            or contacter = '',
            new.contacter,
            contacter
        ),
        `phone` = if(
            phone is null 
            or phone = '',
            new.phone,
            phone
        ),
        `fax` = if(fax is null 
            or fax = '', new.fax, fax),
        `mobile` = if(
            mobile is null 
            or mobile = '',
            new.fax,
            mobile
        ),
        `email` = if(
            email is null 
            or email = '',
            new.email,
            email
        ),
        `qq` = if(qq is null 
            or qq = '', new.email, qq) 
    where name = new.name ;
    else 
    insert into vi_enterprise (
        `name`,
        `account`,
        `category`,
        `category_code`,
        `nature`,
        `nature_code`,
        `scale`,
        `scale_code`,
        `tag`,
        `establish`,
        `introduction`,
        `area`,
        `area_code`,
        `address`,
        `lbs_lon`,
        `lbs_lat`,
        `website`,
        `orgains`,
        `license`,
        `contacter`,
        `public_contact`,
        `phone`,
        `fax`,
        `mobile`,
        `email`,
        `qq`,
        `data_src`,
        `data_key`,
        `update_date`,
        `create_date`,
        `role`,
        `legalize`
    ) 
    values
        (
            new.`name`,
            new.`account`,
            new.`category`,
            new.`category_code`,
            new.`nature`,
            new.`nature_code`,
            new.`scale`,
            new.`scale_code`,
            new.`tag`,
            new.`establish`,
            new.`introduction`,
            new.`area`,
            new.`area_code`,
            new.`address`,
            new.`lbs_lon`,
            new.`lbs_lat`,
            new.`website`,
            new.`orgains`,
            new.`license`,
            new.`contacter`,
            new.`public_contact`,
            new.`phone`,
            new.`fax`,
            new.`mobile`,
            new.`email`,
            new.`qq`,
            'snaker',
            new.name,
            new.`update_date`,
            new.`create_date`,
            new.`role`,
            new.`legalize`
        ) ;
    end if ;
end;
$$

delimiter ;
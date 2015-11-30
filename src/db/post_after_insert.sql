delimiter $$

drop trigger `post_after_insert`$$

create
    trigger `post_after_insert` after insert on `db_entpost` 
    for each row begin
    declare de_id bigint (20) ;
    declare de_ent_name varchar (255) ;
    declare de_area_code varchar (20) ;
    declare de_data_key varchar (255) ;
    declare de_count integer (11) ;
    declare de_area_name varchar (20) ;
    set de_count = 0 ;
    set de_id = 0 ;
    set de_area_name='';
    set de_area_code='';
    set de_count=0;
    select 
        id,
        name,
        area_code into de_id,
        de_ent_name,
        de_area_code 
    from
        db_enterprise 
    where data_key = new.data_ent_key 
        and data_src = new.data_src ;
    if de_id > 0 
    then 
    select 
        name into de_area_name 
    from
        ut_area 
    where code = de_area_code limit 1;
    set de_data_key = concat(
        de_ent_name,
        '-',
        new.name,
        '-',
        de_area_name
    ) ;
    select 
        count(*) into de_count 
    from
        vi_entpost 
    where data_key = de_data_key ;
    if de_count = 0 
    then 
    insert into `vi_entpost` (
        `name`,
        `category`,
        `category_code`,
        `nature`,
        `nature_code`,
        `headcount`,
        `age`,
        `gender`,
        `salary`,
        `experience`,
        `experience_code`,
        `education`,
        `education_code`,
        `tag`,
        `introduction`,
        `area`,
        `area_code`,
        `address`,
        `lbs_lon`,
        `lbs_lat`,
        `data_src`,
        `data_key`,
        `data_ent_key`,
        `update_date`,
        `create_date`,
         salary_type
    ) 
    values
        (
            new.name,
            new.category,
            new.category_code,
            new.nature,
            new.nature_code,
            new.headcount,
            new.age,
            new.gender,
            new.salary,
            new.experience,
            new.experience_code,
            new.education,
            new.education_code,
            new.tag,
            new.introduction,
            new.area,
            new.area_code,
            new.address,
            new.lbs_lon,
            new.lbs_lat,
            'snaker',
            de_data_key,
            de_ent_name,
            new.update_date,
            new.create_date,
            new.salary_type
        ) ;
    end if ;
    end if ;
end;
$$

delimiter ;
delimiter $$

drop procedure if exists `save_or_update_post`$$

create procedure `save_or_update_post`(
    in name varchar (255),
    in categorycode varchar (255),
    in naturecode varchar (255),
    in headcount varchar (255),
    in age varchar (255),
    in gender varchar (255),
    in salary varchar (255),
    in tag varchar (255),
    in introduction text,
    in areacode varchar (255),
    in address varchar (255),
    in lbslon varchar (255),
    in lbslat varchar (255),
    in datasrc varchar (255),
    in datakey varchar (255),
    in dataentkey varchar (255),
    in updatedate varchar (255),
    in createdate varchar (255),
    in experiencecode varchar (255),
    in educationcode varchar (255),
    in salarytype varchar(255),
    out msg varchar(255),
    out issuccess integer (11)
)
begin
    
    declare pid bigint (20) ;
    declare lbsid bigint (20) ;
    declare eid bigint (20) ;
    declare ename varchar (200) ;
    declare industry varchar (20) ;
    declare property varchar (20) ;
    declare employ_num varchar (20) ;
    declare ageid bigint (20) ;
    declare genderid bigint (20) ;
    declare experiencecodeid bigint (20) ;
    declare educationcodeid bigint (20) ;
    declare epsid bigint (20) ;
    declare vepid bigint (20) ;
    declare is_several integer (11) ;
    set lbsid = 0 ;
    set eid = 0 ;
    set pid = 0 ;
    set ename = "" ;
    set industry =null ;  
    set property =null  ;  
    set employ_num =null ; 
    set ageid = 0 ;
    set genderid = 0 ;
    set experiencecodeid = 0 ;
    set educationcodeid = 0 ;
    set epsid = 0 ;
    set vepid = 0 ;
    set is_several = 0 ;
    set msg='';
    set issuccess=1;
    set createdate=if(createdate is null or createdate='',now(),createdate);
    set updatedate=if(updatedate is null or updatedate='',createdate,updatedate);
    set lbslon=if(lbslon ='',null,lbslon);
    set lbslat=if(lbslat ='',null,lbslat);
  
    
    
    
    if name is null or name =''
    then
    set msg ='岗位不能空';
    set issuccess=0;
    end if;
    
    
    if categorycode is null or categorycode =''
    then
    set msg =concat(msg,'岗位类别不能为空');
    set issuccess=0;
    end if;
    
    
    if naturecode ='' or naturecode is null
    then
    set naturecode ='007.001';
    end if;
    
    
    
    if  headcount is null or headcount ='' or headcount = - 1
    then 
    set headcount = 10 ;
    set is_several = 1 ;
    end if ;
    
    
    
    if  age is null or age ='' or age = - 1
    then 
    set age = "00-99" ;
    end if ;
    
    
    
    if  gender =1
    then 
    set gender = "003.001" ;
    elseif gender =0
    then
    set gender='003.002';
    else
    set gender='003.003';
    end if ;
    
    
    if salary is null or salary='' or salary=-1
    then
    set salary=0;
    end if;
    
    
    if experiencecode is null or experiencecode='' 
    then
    set experiencecode='005.009';
    end if;
    
    
    if educationcode is null or educationcode=''
    then 
    set educationcode ='004.011';
    end if;
    
    
    if dataentkey is null or dataentkey =''
    then
    set msg =concat(msg,'dataentkey 不能为空');
    set issuccess=0; 
    end if;
    
    select 
        ent_id,
        ent_name,
        industry,
        property,
        employ_num into eid,
        ename,
        industry,
        property,
        employ_num 
    from
        zcdh_ent_enterprise 
    where data_key = dataentkey 
    limit 1 ;
    
    if eid=0
    then 
    set msg =concat(msg,'dataentkey 找不到');
    set issuccess=0; 
    end if;
    
    if issuccess =1
    then
	    select 
		id into pid 
	    from
		zcdh_ent_post 
	    where data_key = datakey 
		and data_src = datasrc ;
	    if pid = 0 
	    then 
	  
	    
	    insert into `zcdh_ent_lbs` (`latitude`, `longitude`) 
	    values
		(lbslat, lbslon) ;
		
	    select 
		@@identity into lbsid ;
	    insert into `zcdh_ent_post` (
		`ent_id`,
		`expire_date`,
		`headcounts`,
		`lbs_id`,
		`parea`,
		`pjob_category`,
		`post_address`,
		`post_code`,
		`post_remark`,
		`psalary`,
		`publish_date`,
		`post_aliases`,
		`is_several`,
		`tag_selected`,
		`update_date`,
		`data_key`,
		`data_src`,
		`data_ent_key`,
		salary_type
	    ) 
	    values
		(
		    eid,
		    date_add(createdate, interval 5 day),
		    headcount,
		    lbsid,
		    areacode,
		    naturecode,
		    address,
		    categorycode,
		    introduction,
		    salary,
		    createdate,
		    name,
		    is_several,
		    tag,
		    updatedate,
		    datakey,
		    datasrc,
		    dataentkey,
		    salarytype
		) ;
	    select 
		@@identity into pid ;
		
	    else 
	    select 
		lbs_id into lbsid 
	    from
		zcdh_ent_post 
	    where id = pid ;
	    if lbsid = 0 
	    then 
	    insert into `zcdh_ent_lbs` (`latitude`, `longitude`) 
	    values
		(lbslat, lbslon) ;
	    select 
		@@identity into lbsid ;
	    else 
	    update 
		zcdh_ent_lbs 
	    set
		latitude = lbslat,
		longitude = lbslon 
	    where lbs_id = lbsid ;
	    end if ;
	    update 
		zcdh_ent_post 
	    set
		`ent_id` = eid,
		`expire_date` = date_add(createdate, interval 5 day),
		`headcounts` = headcount,
		`lbs_id` = lbsid,
		`parea` = areacode,
		`pjob_category` = naturecode,
		`post_address` = address,
		`post_code` = categorycode,
		`post_remark` = introduction,
		`psalary` = salary,
		`publish_date` = createdate,
		`post_aliases` = name,
		`is_several` = is_several,
		`tag_selected` = tag,
		`update_date` = now(),
		`data_key` = datakey,
		`data_src` = datasrc,
		`data_ent_key` = dataentkey ,
		`salary_type`=salarytype
	    where id = pid ;
	    end if ;
	    
	    select 
		ear.ent_ability_id into ageid 
	    from
		zcdh_ent_ability_require ear 
	    where ear.`technology_code` = '-0000000000001' 
		and ear.post_id = pid 
	    limit 1 ;
	    if ageid = 0 
	    then 
	    insert into `zcdh_ent_ability_require` (
		`ent_id`,
		`param_code`,
		`post_id`,
		technology_code
	    ) 
	    values
		(eid, age, pid, '-0000000000001') ;
	    else 
	    update 
		zcdh_ent_ability_require 
	    set
		ent_id = eid,
		param_code = age,
		post_id = pid,
		technology_code = '-0000000000001' 
	    where ent_ability_id = ageid ;
	    end if ;
	    select 
		ear.ent_ability_id into genderid 
	    from
		zcdh_ent_ability_require ear 
	    where ear.`technology_code` = '-0000000000002' 
		and ear.post_id = pid 
	    limit 1 ;
	    if genderid = 0 
	    then 
	    insert into `zcdh_ent_ability_require` (
		`ent_id`,
		`param_code`,
		`post_id`,
		technology_code
	    ) 
	    values
		(eid, gender, pid, '-0000000000002') ;
	    else 
	    update 
		zcdh_ent_ability_require 
	    set
		ent_id = eid,
		param_code = gender,
		post_id = pid,
		technology_code = '-0000000000002' 
	    where ent_ability_id = genderid ;
	    end if ;
	    select 
		ear.ent_ability_id into experiencecodeid 
	    from
		zcdh_ent_ability_require ear 
	    where ear.`technology_code` = '-0000000000003' 
		and ear.post_id = pid 
	    limit 1 ;
	    if genderid = 0 
	    then 
	    insert into `zcdh_ent_ability_require` (
		`ent_id`,
		`param_code`,
		`post_id`,
		technology_code
	    ) 
	    values
		(
		    eid,
		    experiencecode,
		    pid,
		    '-0000000000003'
		) ;
	    else 
	    update 
		zcdh_ent_ability_require 
	    set
		ent_id = eid,
		param_code = experiencecode,
		post_id = pid,
		technology_code = '-0000000000003' 
	    where ent_ability_id = experiencecodeid ;
	    end if ;
	    select 
		ear.ent_ability_id into educationcodeid 
	    from
		zcdh_ent_ability_require ear 
	    where ear.`technology_code` = '-0000000000004' 
		and ear.post_id = pid 
	    limit 1 ;
	    if educationcodeid = 0 
	    then 
	    insert into `zcdh_ent_ability_require` (
		`ent_id`,
		`param_code`,
		`post_id`,
		technology_code
	    ) 
	    values
		(
		    eid,
		    educationcode,
		    pid,
		    '-0000000000004'
		) ;
	    else 
	    update 
		zcdh_ent_ability_require 
	    set
		ent_id = eid,
		param_code = educationcode,
		post_id = pid,
		technology_code = '-0000000000004' 
	    where ent_ability_id = educationcodeid ;
	    
	    end if ;
	    select 
		ps_id into epsid 
	    from
		zcdh_ent_post_status 
	    where post_id = pid ;
	    
	    if epsid = 0 
	    then 
	    insert into zcdh_ent_post_status (
		post_id,
		post_status,
		employ_total,
		un_employ
	    ) 
	    values
		(pid, 1, headcount, headcount) ;
	    else 
	    update 
		zcdh_ent_post_status 
	    set
		post_id = pid,
		post_status = 1,
		employ_total = headcount,
		un_employ = headcount 
	    where post_id = pid ;
	    end if ;
	    
	    select 
		id into vepid 
	    from
		zcdh_view_ent_post 
	    where post_id = pid ;
	    
	    if vepid = 0 
	    then 
	    insert into `zcdh_view_ent_post` (
		`post_id`,
		`lat`,
		`lon`,
		`tag_selected`,
		`post_aliases`,
		`ent_id`,
		`ent_name`,
		`industry`,
		`property`,
		`employ_num`,
		`area_code`,
		`salary_code`,
		`post_code`,
		`publish_date`,
		`max_salary`,
		`min_salary`,
		`post_property_code`,
		`salary_type`
	    ) 
	    values
		(
		    pid,
		    lbslat,
		    lbslon,
		    tag,
		    name,
		    eid,
		    ename,
		    industry,
		    property,
		    employ_num,
		    areacode,
		    salary,
		    categorycode,
		    createdate,
		    if(
			salary is null 
			and salary = '',
			null,
			if(
			    locate('-',salary) > 0,
			   right(salary, length(salary)-locate('-',salary)),
			    salary
			)
		    ),
		    if(
			salary is null 
			and salary = '',
			null,
			if(
			    locate('-',salary) > 0,
			    left(salary, locate('-',salary) - 1),
			    salary
			)
		    ),
		    naturecode,
		    1
		) ;
	    else 
	    update 
		`zcdh_view_ent_post` 
	    set
		`post_id` = pid,
		`lat` = lbslat,
		`lon` = lbslon,
		`tag_selected` = tag,
		`post_aliases` = name,
		`ent_id` = eid,
		`ent_name` = ename,
		`industry` = industry,
		`property` = property,
		`employ_num` = employ_num,
		`area_code` = areacode,
		`salary_code` = salary,
		`post_code` = categorycode,
		`publish_date` = createdate,
		`max_salary` = if(
		    salary is null 
		    and salary = '',
		    null,
		    if(
			locate('-',salary) > 0,
			right(salary, length(salary)-locate('-',salary)),
			salary
		    )
		),
		`min_salary` = if(
		    salary is null 
		    and salary = '',
		    null,
		    if(
			locate('-',salary) > 0,
			left(salary, locate('-',salary) - 1),
			salary
		    )
		),
		`post_property_code` = naturecode,
		`salary_type` = 1 
	    where id = vepid ;
	    end if ;
	    
    end if;
   
end$$

delimiter ;
delimiter $$

drop procedure if exists `save_or_update_ent`$$

create procedure `save_or_update_ent`(
    in name varchar (255),
    in categorycode varchar (255),
    in naturecode varchar (255),
    in scalecode varchar (255),
    in tag varchar (255),
    in establish varchar (255),
    in introduction text,
    in address varchar (255),
    in website varchar (255),
    in areacode varchar (255),
    in lbslon varchar (255),
    in lbslat varchar (255),
    in orgains varchar (255),
    in license varchar (255),
    in contacter varchar (255),
    in publiccontact varchar (255),
    in phone varchar (255),
    in fax varchar (255),
    in mobile varchar (255),
    in email varchar (255),
    in qq varchar (255),
    in datasrc varchar (255),
    in datakey varchar (255),
    in updatedate varchar (255),
    in createdate varchar (255),
    in de_account varchar (255),
    in entrole varchar(255),
    in de_islegalize varchar(255),
    out msg varchar(255),
    out issuccess integer (11)
)
begin
    declare eid bigint (20) ;
    declare lbsid bigint (20) ;
    declare accountid bigint (20) ;
    declare lbscount integer (11) ;
   
    declare crecount integer(11);
  
   
    set issuccess =1;
    set eid = 0 ;
    set lbsid = 0 ;
    set accountid = 0 ;
    set lbscount = 0 ;
    set msg ='';
    set crecount =0;
    set establish = if(establish='' or establish is null,now(),establish);
    set createdate = if(createdate='' or createdate is null,now(),createdate);
    set updatedate = if(updatedate='' or updatedate is null,now(),updatedate);
    set lbslon= if(lbslon='' or lbslon is null ,null,lbslon);
    set lbslat= if(lbslat='' or lbslat is null ,null,lbslat);
     
    select 
        ent_id into eid 
    from
        zcdh_ent_enterprise 
    where data_key = datakey 
        and data_src = datasrc limit 1 ; 
        
        
    
    
    
    if name is null or name=''
    then
    set msg="企业名称 不能为空";
    set issuccess=0;
    end if;
    
      
    if categorycode is null or categorycode=''
    then
    set msg=concat(msg,";所属行业编码 不能为空");
    set issuccess=0;
    end if;
      
    if naturecode is null or naturecode=''
    then
    set msg=concat(msg,";行业性质编码 不能为空");
    set issuccess=0;
    end if;
      
    if scalecode is null or scalecode=''
    then
     set scalecode = null;
    end if;
    
    if entrole is null or entrole =''
    then
    set entrole =2;
    end if;
   
    if eid!=0
    then
	
	    if (de_account is null or de_account='')
	    then
		set msg=concat(msg,";账号 不能为空");
		set issuccess=0;
	    else
		select account_id into accountid from zcdh_ent_account where account=de_account limit 1;
		if accountid =0
		then
		set msg=concat(msg,";账号已存在");
		set issuccess=0;
		end if;
	    end if;
    end if;
    
    
    
    if issuccess =1
    then
   
        
	    if eid = 0 
	    then 
	    
	    
	    insert into `zcdh_ent_lbs` (`latitude`, `longitude`) 
	    values
		(lbslat, lbslon) ;
		
	    select 
		@@identity into lbsid ;
		
	    insert into `zcdh_ent_enterprise` (
		`address`,
		`contact`,
		`email`,
		`employ_num`,
		`ent_name`,
		`ent_web`,
		`establish_date`,
		`fax`,
		`industry`,
		`introduction`,
		`is_public_phone`,
		lbs_id,
		`mobile`,
		`parea`,
		`phone`,
		`property`,
		`is_public_mobile`,
		`create_date`,
		`tag_selected`,
		`short_name`,
		`domain`,
		`tencent_qq`,
		`update_date`,
		`data_key`,
		`data_src`,
		 islegalize
	    ) 
	    values
		(
		    address,
		    contacter,
		    email,
		    scalecode,
		    name,
		    website,
		    establish,
		    fax,
		    categorycode,
		    introduction,
		    publiccontact,
		    lbsid,
		    mobile,
		    areacode,
		    phone,
		    naturecode,
		    publiccontact,
		    createdate,
		    tag,
		    left(name, 4),
		    null,
		    qq,
		    updatedate,
		    datakey,
		    datasrc,
		    de_islegalize
		) ;
		
	    select 
		@@identity into eid ;
		
	    select 
		account_id into accountid 
	    from
		zcdh_ent_account 
	    where account = de_account limit 1;
	    if accountid = 0 
	    then 
	    
	    insert into `zcdh_ent_account` (
		`account`,
		`create_date`,
		`ent_id`,
		`pwd`,
		`status`
	    ) 
	    values
		(de_account, now(), eid, "4qrcoum6wau+vubx8g+ipg==", 1) ;
		
	    insert into `zcdh_user_role_rel` (`role_id`, `user_id`) 
	    values
		(entrole, eid) ;
		
	    end if ;
	    else 
	    
	    
	    select 
		lbs_id into lbsid 
	    from
		zcdh_ent_enterprise 
	    where ent_id = eid limit 1;
	    
	    select 
		count(1) into lbscount 
	    from
		zcdh_ent_lbs 
	    where lbs_id = lbsid ;
	    if lbscount = 0 
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
		zcdh_ent_enterprise 
	    set
		address = address,
		contact = contacter,
		email = email,
		employ_num = scalecode,
		ent_name = name,
		ent_web = website,
		establish_date = establish,
		fax = fax,
		industry = categorycode,
		introduction = introduction,
		is_public_phone = publiccontact,
		lbs_id = lbsid,
		mobile = mobile,
		parea = areacode,
		phone = phone,
		property = naturecode,
		is_public_mobile = publiccontact,
		create_date =createdate ,
		tag_selected = tag,
		short_name = left(name, 4),
		tencent_qq = qq,
		update_date = updatedate,
		data_key = datakey,
		data_src = datasrc,
		islegalize = de_islegalize 
	    where ent_id = eid ;
	    end if ;
	    
	    select 
		count(1) crecount 
	    from
		zcdh_ent_credentials 
	    where ent_id = eid ;
	    
	    if crecount = 0 
	    then 
		
		
	    if (orgains is null 
		or orgains = '') 
	    and license is not null 
	    and license != '' 
	    then 
	    
	    insert into zcdh_ent_credentials (ent_id, type, credential_num) 
	    values
		(eid, '001', license) ;
	    
	    elseif (license is null 
		or license = '') 
	    and orgains is not null 
	    and orgains != '' 
	    then 
	    insert into zcdh_ent_credentials (ent_id, type, credential_num) 
	    values
		(eid, '002', orgains) 
		;
	    
	    end if ;
	    else
	    
	    
	     if (orgains is null 
		or orgains = '') 
	    and license is not null 
	    and license != '' 
	    then 
	    update 
		zcdh_ent_credentials 
	    set
		type = '001',
		credential_num = license 
	    where ent_id = eid ;
	    elseif (license is null 
		or license = '') 
	    and orgains is not null 
	    and orgains != '' 
	    then 
	    
	    update 
		zcdh_ent_credentials 
	    set
		type = '002',
		credential_num = orgains 
	    where ent_id = eid ;
	    else 
	    delete 
	    from
		zcdh_ent_credentials 
	    where ent_id = eid ;
	    end if ;
	    end if ;
	   
    end if;
end$$

delimiter ;
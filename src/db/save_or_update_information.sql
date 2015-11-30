delimiter $$

drop procedure if exists `save_or_update_information`$$

create procedure `save_or_update_information`(
in informationcode varchar (255),
in title varchar (255),
in source varchar (255),
in contenttype varchar(255),
in html_content varchar(255) ,
in htmlname varchar(255), 
in accountid varchar(255),
in create_time varchar(255),
in is_draft varchar(255),
in summary varchar(255),
in file_code varchar(255),
in is_cover varchar(255),
in last_ip varchar(255),
in last_account_id varchar(255),
in last_modify_time varchar(255),
in order_type varchar(255),
in orders varchar(255),
in appfuncntioncodes varchar (255),
in datasrc varchar(255),
in datakey varchar(255),
out msg varchar(255),
out issuccess varchar(255)
)
begin
declare id bigint(20);
set msg='';
set id =0;
select id into id from zcdh_admin_information  where data_key=datakey and datasrc=data_src limit 1;
if(id =0)
then 
	insert into `zcdh_admin_information` (
	    `account_id`,
	    `content_type`,
	    `create_time`,
	    `file_code`,
	    `html_content`,
	    `html_name`,
	    `information_code`,
	    `is_cover`,
	    `is_draft`,
	    `last_account_id`,
	    `last_ip`,
	    `last_modify_time`,
	    `source`,
	    `summary`,
	    `title`,
	    `order_type`,
	    `orders`,
	    `data_key`,
	    `data_src`
	) 
	values
	    (
		accountid,
		contenttype,
		create_time,
		file_code,
		html_content,
		htmlname,
		informationcode,
		is_cover,
		is_draft,
		last_account_id,
		last_ip,
		last_modify_time,
		source,
		summary,
		title,
		order_type,
		orders,
		datakey,
		datasrc
	    ) ;
else
	update 
	    `zcdh_admin_information` 
	set
	    `account_id` = accountid,
	    `content_type` = contenttype,
	    `create_time` = create_time,
	    `file_code` = file_code,
	    `html_content` = html_content,
	    `html_name` = htmlname,
	    `information_code` = informationcode,
	    `is_cover` = is_cover,
	    `is_draft` = is_draft,
	    `last_account_id` = last_account_id,
	    `last_ip` = last_ip,
	    `last_modify_time` = last_modify_time,
	    `source` = source,
	    `summary` = summary,
	    `title` = title,
	    `order_type` = order_type,
	    `orders` = orders 
	where `id` = id;
end if;
select id into id from zcdh_admin_information  where data_key=datakey and datasrc=data_src limit 1;
delete from `zcdh_admin_app_function_rel` where `information_id`=id;
if(appfuncntioncodes is not null and appfuncntioncodes!='')
then 
	if(locate(',',appfuncntioncodes)>0)
	then
	insert zcdh_admin_app_function_rel(information_id,function_code) values(id,left(appfuncntioncodes,3));
	insert zcdh_admin_app_function_rel(information_id,function_code) values(id,right(appfuncntioncodes,3));
	else
	insert zcdh_admin_app_function_rel(information_id,function_code) values(id,left(appfuncntioncodes,3));
	end if;
end if;
set issuccess=1;
end$$

delimiter ;
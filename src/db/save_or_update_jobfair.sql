delimiter $$

drop procedure if exists `save_or_update_jobfair`$$

create procedure `save_or_update_jobfair`(
    in de_title varchar (255),
    in de_intro varchar (255),
    in de_fair_type varchar (255),
    ##类型 0 社会招聘，1 校园招聘，2 其他
    in de_start_time varchar (255),
    in de_end_time varchar (255),
    in de_contacts varchar (255),
    in de_phone varchar (255),
    in de_area_code varchar (255),
    in de_address varchar (255),
    in de_signup_start_time varchar (255),
    in de_signup_end_time varchar (255),
    in de_remark varchar (255),
    in de_data_src varchar (255),
    in de_data_key varchar (255),
    in de_status varchar (255),
    in de_sign_status varchar (255),
    ##是否允许报名 0:不允许报名，1：允许报名
    in de_signintype varchar (255),
    ##签到方式 (1-按钮签到 2-扫码签到)
    in de_url_type varchar (255),
    ##招聘会主页模板  0 系统，1 手动输入url
    in de_url varchar (255),
    ## urltype=1时  输入的url
    in data_org_key varchar (255), ## 主办方 data_key
    out msg varchar (255),
    out issuccess integer (11) 
)
begin
    declare de_id bigint ;
    declare de_fair_id bigint ;
    declare de_org_id bigint ;
    declare de_poster_set_id bigint;
    set de_poster_set_id=0;
    set de_org_id = 0 ;
    set de_fair_id = 0 ;
    set de_id = 0 ;
    set issuccess = 1 ;
    
    if data_org_key is not null 
    and data_org_key != '' 
    then 
	    select 
		org_id into de_org_id 
	    from
		zcdh_jobfair_organization 
	    where data_key = data_org_key 
	    limit 1 ;
	    if de_org_id =  0 
	    then 
		    set msg = concat(
			msg,
			"主办方唯一标识找不到"
		    ) ;
	    set issuccess = 0 ;
	    end if ;
    else
      set msg = concat(
		msg,
		"主办方唯一标识不能为空"
	    ) ;
    set issuccess = 0 ;
    end if ;
    if issuccess > 0 
    then 
	    select 
		id into de_id 
	    from
		zcdh_jobfair 
	    where data_src = de_data_src 
		and data_key = de_data_key limit 1;
	    if de_id != 0 
	    then 
		    insert into zcdh_jobfair (
			title,
			intro,
			fair_type,
			start_time,
			end_time,
			contacts,
			phone,
			area_code,
			address,
			signup_start_time,
			signup_end_time,
			remark,
			data_src,
			data_key,
			status,
			sign_status,
			signintype,
			org_id,url_type,url
		    ) value (
			de_title,
			de_intro,
			de_fair_type,
			de_start_time,
			de_end_time,
			de_contacts,
			de_phone,
			de_area_code,
			de_address,
			de_signup_start_time,
			de_signup_end_time,
			de_remark,
			de_data_src,
			de_data_key,
			de_status,
			de_sign_status,
			de_signintype,de_org_id,de_url_type,de_url
		    ) ;
		    select 
			@@identity into de_id ;
	    else 
		    update 
			zcdh_jobfair 
		    set
			title = de_title,
			intro = de_intro,
			fair_type = de_fair_type,
			start_time = de_start_time,
			end_time = de_end_time,
			contacts = de_contacts,
			phone = de_phone,
			area_code = de_area_code,
			address = de_address,
			signup_start_time = de_signup_start_time,
			signup_end_time = de_signup_end_time,
			remark = de_remark,
			data_src = de_data_src,
			data_key = de_data_key ,
			org_id=de_org_id
		    where id = de_id ;
	    end if ;
	    select 
		fair_id into de_fair_id 
	    from
		`zcdh_jobfair_propaganda` 
	    where fair_id = de_id limit 1;
	    if de_fair_id = 0 
	    then 
	    insert into `zcdh_jobfair_propaganda` (
		    
		    `fair_id`,
		    `propaganda_content`,
		    `templet_id`
		) 
		values
		    (
			
			de_id,
			'{"wechatpic":"","fairtype":"0","headpic":"","freetemplate":"%5b%7b%22id%22:%221%22,%20%22name%22:%22form%22,%20%22value%22%20:%20%5b%7b%22name%22:%20%22title%22,%20%22value%22:%20%22%e6%8b%9b%e8%81%98%e4%bc%9a%e4%bb%8b%e7%bb%8d%22%20%7d,%20%7b%22name%22:%20%22text%22,%20%22value%22:%20%22%22%20%7d%5d%7d%5d","themecolor":"#ff0000"}',
			2
		    ) ;
	    end if ;
	    
	    select id into de_poster_set_id from zcdh_jobfair_poster_set where fair_id=de_id limit 1;
	    if de_poster_set_id =0
	    then
		insert into `zcdh_jobfair_poster_set` (
		    
		    `activity_title`,
		    `fair_id`,
		    `p_describe`,
		    `poster_img_url`,
		    `poster_size`,
		    `poster_type`,
		    `show_booth_no`,
		    `show_describe`,
		    `show_ent_name`,
		    `show_tips`,
		    `show_title`,
		    `signintype`,
		    `themecolor`,
		    `tips`
		) 
		values
		    (
			
			'',
			de_id,
			'',
			'',
			1,
			1,
			'1',
			'1',
			'1',
			'1',
			'1',
			null,
			'#ff0000',
			''
		    ) ;	    
	    end if;
    end if ;
end$$

delimiter ;
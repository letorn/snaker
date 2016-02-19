-- 2016-02-16 李伟棠

delimiter $$

drop trigger ent_after_update$$

create trigger ent_after_update after update on db_enterprise for each row begin
	update 
		vi_enterprise 
	set
		orgains = if(
			new.orgains is not null and length(trim(new.orgains)) > 0,
			new.orgains,
			orgains
		),
		license = if(
			new.license is not null and length(trim(new.license)) > 0,
			new.license,
			license
		),
		contacter = if(
			new.contacter is not null and length(trim(new.contacter)) > 0,
			new.contacter,
			contacter
		),
		phone = if(
			new.phone is not null and length(trim(new.phone)) > 0,
			new.phone,
			phone
		),
		fax = if(
			new.fax is not null and length(trim(new.fax)) > 0,
			new.fax,
			fax
		),
		mobile = if(
			new.mobile is not null and length(trim(new.mobile)) > 0,
			new.mobile,
			mobile
		),
		email = if(
			new.email is not null and length(trim(new.email)) > 0,
			new.email,
			email
		),
		qq = if(
			new.qq is not null and length(trim(new.qq)) > 0,
			new.qq,
			qq
		),
		update_date = if(
			new.update_date is not null,
			new.update_date,
			now()
		),
		syn_status = 2,
		syn_date = now(),
		syn_message = null
	where data_src = 'snaker'
		and data_key = new.name;
end;
$$

delimiter ;
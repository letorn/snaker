-- 2016-02-16 李伟棠

delimiter $$

drop trigger ent_after_insert$$

create trigger ent_after_insert after insert on db_enterprise for each row begin
	declare de_ent_id bigint;
	set de_ent_id = 0;

	select 
		id into de_ent_id 
	from
		vi_enterprise 
	where data_src = 'snaker'
		and data_key = new.name
	limit 1;

	if de_ent_id != 0 then 
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
		where id = de_ent_id;
	else 
		insert into vi_enterprise(
			name,
			legalize,
			account,
			role,
			category,
			category_code,
			nature,
			nature_code,
			scale,
			scale_code,
			tag,
			establish,
			introduction,
			area,
			area_code,
			address,
			lbs_lon,
			lbs_lat,
			website,
			orgains,
			license,
			contacter,
			public_contact,
			phone,
			fax,
			mobile,
			email,
			qq,
			data_src,
			data_key,
			update_date,
			create_date
		) values(
			new.name,
			if(
				new.legalize is not null,
				new.legalize,
				0
			),
			new.account,
			if(
				new.role is not null,
				new.role,
				2
			),
			new.category,
			new.category_code,
			new.nature,
			new.nature_code,
			new.scale,
			new.scale_code,
			new.tag,
			new.establish,
			new.introduction,
			new.area,
			new.area_code,
			new.address,
			new.lbs_lon,
			new.lbs_lat,
			new.website,
			new.orgains,
			new.license,
			new.contacter,
			if(
				new.public_contact is not null,
				new.public_contact,
				0
			),
			new.phone,
			new.fax,
			new.mobile,
			new.email,
			new.qq,
			'snaker',
			new.name,
			if(
				new.update_date is not null,
				new.update_date,
				now()
			),
			if(
				new.create_date is not null,
				new.create_date,
				now()
			)
		);
	end if;
end;
$$

delimiter ;
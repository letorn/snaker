-- 2016-02-16 李伟棠

delimiter $$

drop trigger post_after_insert$$

create trigger post_after_insert after insert on db_entpost for each row begin
	declare de_ent_id bigint;
	declare de_ent_name varchar(255);
	declare de_area_name varchar(20);
	declare de_data_key varchar(255);
	declare de_post_id bigint;
	
	set de_ent_id = 0;
	set de_ent_name = '';
	set de_area_name = '';
	set de_data_key = '';
	set de_post_id = 0;

	select 
		id,
		name
			into
		de_ent_id,
		de_ent_name
	from
		db_enterprise 
	where data_src = new.data_src
		and data_key = new.data_ent_key
	limit 1;

	if de_ent_id > 0 then 
		select 
			name into de_area_name 
		from
			ut_area 
		where code = new.area_code
		limit 1;

		set de_data_key = concat(
			de_ent_name,
			'-',
			new.name,
			'-',
			de_area_name
		);

		select 
			id into de_post_id 
		from
			vi_entpost 
		where data_src = 'snaker'
			and data_key = de_data_key
		limit 1;

		if de_post_id != 0 then
			update
				vi_entpost
			set
				update_date = if(
					new.update_date is not null,
					new.update_date,
					now()
				),
				syn_status = 2,
				syn_date = now(),
				syn_message = null
			where id = de_post_id;
		else
			insert into vi_entpost(
				name,
				category,
				category_code,
				nature,
				nature_code,
				headcount,
				age,
				gender,
				salary,
				salary_type,
				experience,
				experience_code,
				education,
				education_code,
				tag,
				introduction,
				area,
				area_code,
				address,
				lbs_lon,
				lbs_lat,
				data_src,
				data_key,
				data_ent_key,
				update_date,
				create_date
			) values(
				new.name,
				new.category,
				new.category_code,
				new.nature,
				new.nature_code,
				if(
					new.headcount is not null,
					new.headcount,
					-1
				),
				if(
					new.age is not null,
					new.age,
					-1
				),
				if(
					new.gender is not null,
					new.gender,
					-1
				),
				if(
					new.salary is not null,
					new.salary,
					-1
				),
				if(
					new.salary_type is not null,
					new.salary_type,
					1
				),
				if(
					new.experience is not null,
					new.experience,
					'不限'
				),
				if(
					new.experience_code is not null,
					new.experience_code,
					'005.009'
				),
				if(
					new.education is not null,
					new.education,
					'不限'
				),
				if(
					new.education_code is not null,
					new.education_code,
					'004.011'
				),
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
	end if;
end;
$$

delimiter ;
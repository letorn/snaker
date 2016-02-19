-- 2016-02-16 李伟棠

delimiter $$

drop trigger post_after_update$$

create trigger post_after_update after update on db_entpost for each row begin
	declare de_ent_id bigint;
	declare de_ent_name varchar(255);
	declare de_area_name varchar(20);
	declare de_data_key varchar(255);
	
	set de_ent_id = 0;
	set de_ent_name = '';
	set de_area_name = '';
	set de_data_key = '';

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
		where data_src = 'snaker'
			and data_key = de_data_key;
	end if;
end;
$$

delimiter ;
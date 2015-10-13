-- drop table if exists wf_process;
create table if not exists wf_process(
	id bigint primary key auto_increment,
	name varchar(50),
	content text,
	update_date datetime,
	create_date datetime
);

-- drop table if exists wf_instance;
create table if not exists wf_instance(
	id bigint primary key auto_increment,
	process_id bigint,
	param text,
	update_date datetime,
	create_date datetime
);

-- drop table if exists wf_record;
create table if not exists wf_record(
	id bigint primary key auto_increment,
	process_id bigint,
	instance_id bigint,
	module varchar(50),
	headers text,
	outputs text,
	create_date datetime
);
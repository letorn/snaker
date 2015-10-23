-- 工作流程
-- drop table if exists wf_process;
create table if not exists wf_process(
	id bigint primary key auto_increment,
	name varchar(50),
	content text,
	update_date datetime,
	create_date datetime
);

-- 流程实例
-- drop table if exists wf_instance;
create table if not exists wf_instance(
	id bigint primary key auto_increment,
	process_id bigint,
	param text,
	update_date datetime,
	create_date datetime
);

-- 实例记录
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

-- 企业信息
-- drop table if exists db_enterprise;
create table if not exists db_enterprise(
	id bigint primary key auto_increment,
	name varchar(255),
	account	varchar(255),
	category varchar(255),
	category_code varchar(255),
	nature varchar(255),
	nature_code varchar(255),
	scale varchar(255),
	scale_code varchar(255),
	tag varchar(255),
	establish datetime,
	introduction varchar(255),
	area varchar(255),
	area_code varchar(255),
	address varchar(255),
	lbs_lon double,
	lbs_lat double,
	website varchar(255),
	orgains varchar(255),
	license varchar(255),
	contacter varchar(255),
	public_contact tinyint,
	phone varchar(255),
	fax varchar(255),
	mobile varchar(255),
	email varchar(255),
	qq varchar(255),
	visit_count int,
	data_src varchar(255),
	data_key varchar(255),
	data_url varchar(255),
	data_status tinyint,
	update_date datetime,
	create_date datetime,
	syn_status tinyint,
	unique index db_enterprise_unique_index(data_src, data_key)
);

-- 岗位信息
-- drop table if exists db_entpost;
create table if not exists db_entpost(
	id bigint primary key auto_increment,
	name varchar(255),
	category varchar(255),
	category_code varchar(255),
	nature varchar(255),
	nature_code varchar(255),
	headcount tinyint,
	age varchar(255),
	gender tinyint,
	salary varchar(255),
	experience varchar(255),
	experience_code varchar(255),
	education varchar(255),
	education_code varchar(255),
	tag varchar(255),
	introduction varchar(255),
	area varchar(255),
	area_code varchar(255),
	address varchar(255),
	lbs_lon double,
	lbs_lat double,
	data_src varchar(255),
	data_key varchar(255),
	data_ent_key varchar(255),
	data_url varchar(255),
	data_status tinyint,
	update_date datetime,
	create_date datetime,
	syn_status tinyint,
	unique index db_entpost_unique_index(data_src, data_key)
);

-- 文件
-- drop table if exists sk_file;
create table if not exists sk_file(
	id bigint primary key auto_increment,
	name varchar(100),
	suffix varchar(20),
	ftype varchar(20),
	content longblob
);
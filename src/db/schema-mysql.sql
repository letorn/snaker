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
	params text,
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
	rows mediumtext,
	create_date datetime
);

-- 企业信息
-- drop table if exists db_enterprise;
create table if not exists db_enterprise(
	id bigint primary key auto_increment,
	name varchar(255),
	legalize tinyint,
	account	varchar(255),
	role bigint default 2,
	category varchar(255),
	category_code varchar(255),
	nature varchar(255),
	nature_code varchar(255),
	scale varchar(255),
	scale_code varchar(255),
	tag varchar(255),
	establish datetime,
	introduction text,
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
	update_date datetime default now(),
	create_date datetime default now(),
	syn_status tinyint default -1,
	syn_date datetime,
	syn_message text,
	unique index db_enterprise_unique_index(data_src, data_key),
	index db_enterprise_category_code_index(category_code)
);

-- 企业信息视图- 采集信息合并后存储
-- drop table if exists vi_enterprise;
create table if not exists vi_enterprise(
	id bigint primary key auto_increment,
	name varchar(255),
	legalize tinyint,
	account	varchar(255),
	role bigint default 2,
	category varchar(255),
	category_code varchar(255),
	nature varchar(255),
	nature_code varchar(255),
	scale varchar(255),
	scale_code varchar(255),
	tag varchar(255),
	establish datetime,
	introduction text,
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
	update_date datetime default now(),
	create_date datetime default now(),
	syn_status tinyint default -1,
	syn_date datetime,
	syn_message text,
	unique index vi_enterprise_unique_index(data_src, data_key),
	index vi_enterprise_category_code_index(category_code)
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
	introduction text,
	area varchar(255),
	area_code varchar(255),
	address varchar(255),
	lbs_lon double,
	lbs_lat double,
	data_src varchar(255),
	data_key varchar(255),
	data_ent_key varchar(255),
	update_date datetime default now(),
	create_date datetime default now(),
	syn_status tinyint default -1,
	syn_date datetime,
	syn_message text,
	unique index db_entpost_unique_index(data_src, data_key)
);

-- 岗位信息视图- 采集信息合并后存储
-- drop table if exists vi_entpost;
create table if not exists vi_entpost(
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
	introduction text,
	area varchar(255),
	area_code varchar(255),
	address varchar(255),
	lbs_lon double,
	lbs_lat double,
	data_src varchar(255),
	data_key varchar(255),
	data_ent_key varchar(255),
	update_date datetime default now(),
	create_date datetime default now(),
	syn_status tinyint default -1,
	syn_date datetime,
	syn_message text,
	unique index vi_entpost_unique_index(data_src, data_key)
);

-- 求职者信息
-- drop table if exists db_jobhunter;
create table if not exists db_jobhunter(
	id bigint primary key auto_increment,
	name varchar(255),
	account varchar(255),
	account_status tinyint,
	gender tinyint,
	nation varchar(255),
	mobile varchar(255),
	email varchar(255),
	qq varchar(255),
	experience varchar(255),
	experience_code varchar(255),
	education varchar(255),
	education_code varchar(255),
	major varchar(255),
	household varchar(255),
	polity varchar(255),
	category varchar(255),
	category_code varchar(255),
	hunter_status varchar(255),
	hunter_status_code varchar(255),
	marriage varchar(255),
	cert_type varchar(255),
	cert_id varchar(255),
	birth varchar(255),
	height int,
	weight int,
	location varchar(255),
	location_code varchar(255),
	address varchar(255),
	lbs_lon double,
	lbs_lat double,
	curr_ent varchar(255),
	curr_ent_phone varchar(255),
	curr_post varchar(255),
	curr_post_code varchar(255),
	self_comment text,
	data_src varchar(255),
	data_key varchar(255),
	update_date datetime default now(),
	create_date datetime default now(),
	syn_status tinyint default -1,
	syn_date datetime,
	syn_message text,
	unique index db_jobhunter_unique_index(data_src, data_key),
	index db_jobhunter_curr_post_code_index(curr_post_code)
);

-- 求职者信息视图- 采集信息合并后存储
-- drop table if exists vi_jobhunter;
create table if not exists vi_jobhunter(
	id bigint primary key auto_increment,
	name varchar(255),
	account varchar(255),
	account_status tinyint,
	gender tinyint,
	nation varchar(255),
	mobile varchar(255),
	email varchar(255),
	qq varchar(255),
	experience varchar(255),
	experience_code varchar(255),
	education varchar(255),
	education_code varchar(255),
	major varchar(255),
	household varchar(255),
	polity varchar(255),
	category varchar(255),
	category_code varchar(255),
	hunter_status varchar(255),
	hunter_status_code varchar(255),
	marriage varchar(255),
	cert_type varchar(255),
	cert_id varchar(255),
	birth varchar(255),
	height int,
	weight int,
	location varchar(255),
	location_code varchar(255),
	address varchar(255),
	lbs_lon double,
	lbs_lat double,
	curr_ent varchar(255),
	curr_ent_phone varchar(255),
	curr_post varchar(255),
	curr_post_code varchar(255),
	self_comment text,
	data_src varchar(255),
	data_key varchar(255),
	update_date datetime default now(),
	create_date datetime default now(),
	syn_status tinyint default -1,
	syn_date datetime,
	syn_message text,
	unique index vi_jobhunter_unique_index(data_src, data_key),
	index vi_jobhunter_curr_post_code_index(curr_post_code)
);

-- 宣讲会信息
-- drop table if exists db_talk;
create table if not exists db_talk(
	id bigint primary key auto_increment,
	title varchar(255),
	content longtext,
	source varchar(100),
	data_src varchar(255),
	data_key varchar(255),
	update_date datetime default now(),
	create_date datetime default now(),
	syn_status tinyint default -1,
	syn_date datetime,
	syn_message text,
	unique index db_talk_unique_index(data_src, data_key),
	index db_talk_source_index(source)
);

-- 宣讲会视图- 采集信息合并后存储
-- drop table if exists vi_talk;
create table if not exists vi_talk(
	id bigint primary key auto_increment,
	title varchar(255),
	content longtext,
	source varchar(100),
	data_src varchar(255),
	data_key varchar(255),
	update_date datetime default now(),
	create_date datetime default now(),
	syn_status tinyint default -1,
	syn_date datetime,
	syn_message text,
	unique index vi_talk_unique_index(data_src, data_key),
	index vi_talk_source_index(source)
);

-- 文件
-- drop table if exists sk_file;
create table if not exists sk_file(
	id bigint primary key auto_increment,
	name varchar(100),
	suffix varchar(20),
	ftype varchar(20),
	content longblob,
	update_date datetime,
	unique key sk_file_unique_key(name)
);

-- 工具 日历表
-- drop table if exists ut_date;
create table if not exists ut_date(
	id bigint primary key auto_increment,
	date date,
	year middleint,
	season tinyint,
	month tinyint,
	week tinyint,
	day tinyint
);

-- 工具 职位分类
-- drop table if exists ut_post;
create table if not exists ut_post(
	id bigint primary key auto_increment,
	name varchar(100),
	code varchar(7),
	parent varchar(7)
);

-- 工具 行业分类
-- drop table if exists ut_industry;
create table if not exists ut_industry(
	id bigint primary key auto_increment,
	name varchar(100),
	code varchar(7),
	parent varchar(3)
);

-- 工具 自添加
-- drop table if exists ut_growth;
create table if not exists ut_growth(
	id bigint primary key auto_increment,
	current bigint,
	step tinyint,
	prefix varchar(100),
	len tinyint
);

-- 工具 地区
-- drop table if exists ut_area;
create table if not exists ut_area(
	id bigint primary key auto_increment,
	name varchar(100),
	code varchar(11),
	parent varchar(11)
);
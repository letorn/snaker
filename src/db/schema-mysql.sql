-- drop database if exists snaker_test;
-- create database if not exists snaker_test default character set utf8;
-- use snaker_test;

-- drop database if exists snaker;
-- create database if not exists snaker default character set utf8;
-- use snaker;

-- drop table if exists wf_process;
create table if not exists wf_process(
	id bigint primary key auto_increment,
	name varchar(50),
	content text comment '流程定义',
	update_date datetime,
	create_date datetime
) comment = '工作流程';

-- drop table if exists db_enterprise;
create table if not exists db_enterprise(
	id bigint primary key auto_increment,
	name varchar(255),
	legalize tinyint comment '是否认证. 1 已认证, 0 未认证',
	account	varchar(100),
	role bigint comment '企业权限. 1 所有, 2 部分',
	category varchar(100) comment '所属行业',
	category_code varchar(11),
	nature varchar(100) comment '行业性质',
	nature_code varchar(11),
	scale varchar(50) comment '企业规模',
	scale_code varchar(11),
	tag varchar(255) comment '企业标签',
	establish datetime comment '成立时间',
	introduction text,
	area varchar(50) comment '所在地区. 二级',
	area_code varchar(11),
	address varchar(255),
	lbs_lon double,
	lbs_lat double,
	website varchar(255),
	orgains varchar(20) comment '组织机构代码',
	license varchar(30) comment '工商营业执照',
	contacter varchar(50) comment '联系人',
	public_contact tinyint comment '是否公开联系方式. 1 公开, 0 保密',
	phone varchar(50),
	fax varchar(50),
	mobile varchar(50),
	email varchar(50),
	qq varchar(50),
	data_src varchar(20) comment '数据来源',
	data_key varchar(255) comment '唯一标识',
	update_date datetime default now(),
	create_date datetime default now(),
	unique index db_enterprise_unique_index(data_src, data_key)
) comment = '企业信息. 自动合并到视图表';

-- drop table if exists vi_enterprise;
create table if not exists vi_enterprise(
	id bigint primary key auto_increment,
	name varchar(255),
	legalize tinyint default 0 comment '是否认证. 1 已认证, 0 未认证',
	account	varchar(100),
	role bigint default 2 comment '企业权限. 1 所有, 2 部分',
	category varchar(100) comment '所属行业',
	category_code varchar(11),
	nature varchar(100) comment '行业性质',
	nature_code varchar(11),
	scale varchar(50) comment '企业规模',
	scale_code varchar(11),
	tag varchar(255) comment '企业标签',
	establish datetime comment '成立时间',
	introduction text,
	area varchar(50) comment '所在地区. 二级',
	area_code varchar(11),
	address varchar(255),
	lbs_lon double,
	lbs_lat double,
	website varchar(255),
	orgains varchar(20) comment '组织机构代码',
	license varchar(30) comment '工商营业执照',
	contacter varchar(50) comment '联系人',
	public_contact tinyint default 0 comment '是否公开联系方式. 1 公开, 0 保密',
	phone varchar(50),
	fax varchar(50),
	mobile varchar(50),
	email varchar(50),
	qq varchar(50),
	data_src varchar(20) comment '数据来源',
	data_key varchar(255) comment '唯一标识',
	update_date datetime default now(),
	create_date datetime default now(),
	syn_status tinyint default -1 comment '同步状态. 1 成功, 0 失败, -1 未同步, 2 已修改',
	syn_date datetime,
	syn_message text,
	unique index vi_enterprise_unique_index(data_src, data_key),
	index vi_enterprise_category_code_index(category_code)
) comment = '企业信息视图. 用于上传数据';

-- drop table if exists db_entpost;
create table if not exists db_entpost(
	id bigint primary key auto_increment,
	name varchar(255),
	category varchar(100) comment '职位类别',
	category_code varchar(11),
	nature varchar(50) comment '工作性质',
	nature_code varchar(11),
	headcount mediumint comment '招聘人数. -1 不限',
	age varchar(50) comment '年龄. 24-30, -1 不限',
	gender tinyint comment '性别. 1 男, 0 女, -1 不限',
	salary varchar(50) comment '薪资待遇. 1000-2000, -1 面议',
	salary_type tinyint comment '薪资类型. 1 月薪, 2 时薪, 3 日薪, 4 计次, 5 年薪',
	experience varchar(50) comment '工作年限. 005.009 不限',
	experience_code varchar(11),
	education varchar(50) comment '最低学历. 004.011 不限',
	education_code varchar(11),
	tag varchar(255) comment '职位标签',
	introduction text,
	area varchar(50) comment '工作地区. 二级',
	area_code varchar(11),
	address varchar(255),
	lbs_lon double,
	lbs_lat double,
	data_src varchar(20) comment '数据来源',
	data_key varchar(255) comment '唯一标识',
	data_ent_key varchar(255) comment '关联企业标识',
	update_date datetime default now(),
	create_date datetime default now(),
	unique index db_entpost_unique_index(data_src, data_key)
) comment = '岗位信息. 自动合并到视图表';

-- drop table if exists vi_entpost;
create table if not exists vi_entpost(
	id bigint primary key auto_increment,
	name varchar(255),
	category varchar(100) comment '职位类别',
	category_code varchar(11),
	nature varchar(50) comment '工作性质',
	nature_code varchar(11),
	headcount mediumint default -1 comment '招聘人数. -1 不限',
	age varchar(50) default '-1' comment '年龄. 24-30, -1 不限',
	gender tinyint default -1 comment '性别. 1 男, 0 女, -1 不限',
	salary varchar(50) default -1 comment '薪资待遇. 1000-2000, -1 面议',
	salary_type tinyint default 1 comment '薪资类型. 1 月薪, 2 时薪, 3 日薪, 4 计次, 5 年薪',
	experience varchar(50) default '不限' comment '工作年限. 005.009 不限',
	experience_code varchar(11) default '005.009',
	education varchar(50) default '不限' comment '最低学历. 004.011 不限',
	education_code varchar(11) default '004.011',
	tag varchar(255) comment '职位标签',
	introduction text,
	area varchar(50) comment '工作地区. 二级',
	area_code varchar(11),
	address varchar(255),
	lbs_lon double,
	lbs_lat double,
	data_src varchar(20) comment '数据来源',
	data_key varchar(255) comment '唯一标识',
	data_ent_key varchar(255) comment '关联企业标识',
	update_date datetime default now(),
	create_date datetime default now(),
	syn_status tinyint default -1 comment '同步状态. 1 成功, 0 失败, -1 未同步, 2 已修改',
	syn_date datetime,
	syn_message text,
	unique index vi_entpost_unique_index(data_src, data_key)
) comment = '岗位信息视图. 用于上传数据';

-- drop table if exists db_jobhunter;
create table if not exists db_jobhunter(
	id bigint primary key auto_increment,
	name varchar(255),
	account varchar(50),
	account_status tinyint comment '账号状态. 1 启动, 0 禁用, 2 未激活',
	gender tinyint comment '性别. 1 男, 0 女',
	nation varchar(50) comment '民族',
	mobile varchar(50),
	email varchar(50),
	qq varchar(50),
	experience varchar(50) comment '工作年限',
	experience_code varchar(11),
	education varchar(50) comment '最高学历',
	education_code varchar(11),
	major varchar(50) comment '专业',
	household varchar(50) comment '户口',
	polity varchar(20) comment '政治面貌',
	category varchar(20) comment '人才类型',
	category_code varchar(11),
	hunter_status varchar(50) comment '求职状态. 013.001 求职',
	hunter_status_code varchar(11),
	marriage varchar(20) comment '婚姻. 未婚, 已婚, 离异, 保密' ,
	cert_type varchar(20) comment '证件类型. 身份证, 军人证, 护照证, 其它',
	cert_id varchar(50),
	birth datetime,
	height int,
	weight int,
	location varchar(50) comment '现所在地. 二级',
	location_code varchar(11),
	address varchar(255),
	lbs_lon double,
	lbs_lat double,
	curr_ent varchar(255) comment '现所在企业',
	curr_ent_phone varchar(50) comment '现所在企业号码',
	curr_post varchar(100) comment '现所在企业职位类别',
	curr_post_code varchar(11),
	self_comment text comment '自我评价',
	data_src varchar(20) comment '数据来源',
	data_key varchar(255) comment '唯一标识',
	update_date datetime default now(),
	create_date datetime default now(),
	unique index db_jobhunter_unique_index(data_src, data_key)
) comment = '求职者信息. 自动合并到视图表';

-- drop table if exists vi_jobhunter;
create table if not exists vi_jobhunter(
	id bigint primary key auto_increment,
	name varchar(255),
	account varchar(50),
	account_status tinyint default 2 comment '账号状态. 1 启动, 0 禁用, 2 未激活',
	gender tinyint default 1 comment '性别. 1 男, 0 女',
	nation varchar(50) default '汉族' comment '民族',
	mobile varchar(50),
	email varchar(50),
	qq varchar(50),
	experience varchar(50) comment '工作年限',
	experience_code varchar(11),
	education varchar(50) comment '最高学历',
	education_code varchar(11),
	major varchar(50) comment '专业',
	household varchar(50) comment '户口',
	polity varchar(20) comment '政治面貌',
	category varchar(20) comment '人才类型',
	category_code varchar(11),
	hunter_status varchar(50) default '求职,急寻新工作' comment '求职状态. 013.001 求职',
	hunter_status_code varchar(11) default '013.001',
	marriage varchar(20) default '未婚' comment '婚姻. 未婚, 已婚, 离异, 保密' ,
	cert_type varchar(20) comment '证件类型. 身份证, 军人证, 护照证, 其它',
	cert_id varchar(50),
	birth datetime,
	height int,
	weight int,
	location varchar(50) comment '现所在地. 二级',
	location_code varchar(11),
	address varchar(255),
	lbs_lon double,
	lbs_lat double,
	curr_ent varchar(255) comment '现所在企业',
	curr_ent_phone varchar(50) comment '现所在企业号码',
	curr_post varchar(100) comment '现所在企业职位类别',
	curr_post_code varchar(11),
	self_comment text comment '自我评价',
	data_src varchar(20) comment '数据来源',
	data_key varchar(255) comment '唯一标识',
	update_date datetime default now(),
	create_date datetime default now(),
	syn_status tinyint default -1 comment '同步状态. 1 成功, 0 失败, -1 未同步, 2 已修改',
	syn_date datetime,
	syn_message text,
	unique index vi_jobhunter_unique_index(data_src, data_key),
	index vi_jobhunter_curr_post_code_index(curr_post_code)
) comment = '求职者信息视图. 用于上传数据';

-- drop table if exists db_talk;
create table if not exists db_talk(
	id bigint primary key auto_increment,
	title varchar(255) comment '标题',
	content text comment '内容',
	source varchar(100)  comment '信息来源',
	data_src varchar(20) comment '数据来源',
	data_key varchar(255) comment '唯一标识',
	update_date datetime default now(),
	create_date datetime default now(),
	unique index db_talk_unique_index(data_src, data_key)
) comment = '宣讲会信息. 自动合并到视图表';

-- drop table if exists vi_talk;
create table if not exists vi_talk(
	id bigint primary key auto_increment,
	title varchar(255) comment '标题',
	content text comment '内容',
	source varchar(100)  comment '信息来源',
	data_src varchar(20) comment '数据来源',
	data_key varchar(255) comment '唯一标识',
	update_date datetime default now(),
	create_date datetime default now(),
	syn_status tinyint default -1 comment '同步状态. 1 成功, 0 失败, -1 未同步, 2 已修改',
	syn_date datetime,
	syn_message text,
	unique index vi_talk_unique_index(data_src, data_key),
	index vi_talk_source_index(source)
) comment = '宣讲会视图. 用于上传数据';

-- drop table if exists sk_admin;
create table if not exists sk_admin(
	id bigint primary key auto_increment,
	account varchar(30),
	password varchar(20)
) comment = '管理员';

-- drop table if exists sk_file;
create table if not exists sk_file(
	id bigint primary key auto_increment,
	name varchar(100),
	suffix varchar(20) comment '文件后缀',
	ftype varchar(20) comment '文件用途',
	content longblob,
	update_date datetime,
	unique key sk_file_unique_key(name)
) comment = '文件';

-- drop table if exists sk_config;
create table if not exists sk_config(
	id bigint primary key auto_increment,
	name varchar(50),
	value varchar(100)
) comment = '系统配置';

-- drop table if exists ut_date;
create table if not exists ut_date(
	id bigint primary key auto_increment,
	date date comment '年月日',
	year middleint comment '年',
	season tinyint comment '季',
	month tinyint comment '月',
	week tinyint comment '周',
	day tinyint comment '日'
) comment = '工具-日历表';

-- drop table if exists ut_post;
create table if not exists ut_post(
	id bigint primary key auto_increment,
	name varchar(100),
	code varchar(11),
	parent varchar(11)
) comment = '工具-职位类别';

-- drop table if exists ut_industry;
create table if not exists ut_industry(
	id bigint primary key auto_increment,
	name varchar(100),
	code varchar(11),
	parent varchar(11)
) comment = '工具-所属行业';

-- drop table if exists ut_growth;
create table if not exists ut_growth(
	id bigint primary key auto_increment,
	current bigint comment '当前值',
	step tinyint comment '自增步长',
	prefix varchar(20) comment '前缀',
	len tinyint comment '长度'
) comment = '工具-自添加';

-- drop table if exists ut_area;
create table if not exists ut_area(
	id bigint primary key auto_increment,
	name varchar(50),
	code varchar(11),
	parent varchar(11)
) comment = '工具-地区';
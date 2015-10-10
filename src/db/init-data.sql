create database snaker default character set utf8;
use snaker;
drop table if exists wf_process;
create table if not exists wf_process(
	id bigint primary key auto_increment,
	name varchar(50),
	content text,
	create_date datetime
);
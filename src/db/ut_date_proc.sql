drop procedure if exists ut_date_proc;
delimiter $$
create procedure ut_date_proc(
	in begin_time datetime,
	in end_time datetime
)
begin
	declare temp_time datetime;
	declare year middleint;
	declare season tinyint;
	declare month tinyint;
	declare week tinyint;
	declare day tinyint;

	set temp_time = begin_time;
	while temp_time <= end_time do
		set year = year(temp_time);
		set season = quarter(temp_time);
		set month = month(temp_time);
		set week = dayofweek(temp_time);
		set day = dayofmonth(temp_time);
		insert into ut_date(date,year,season,month,week,day) values(temp_time,year,season,month,week,day);
		set temp_time = adddate(temp_time, 1);
	end while;
end $$
delimiter ;

call ut_date_proc('2010-01-01', '2030-12-31');
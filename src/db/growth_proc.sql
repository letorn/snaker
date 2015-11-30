delimiter $$
drop procedure if exists `growth_proc`$$
create procedure `growth_proc`(in de_prefix varchar(255),out col varchar(255))
begin
declare de_id bigint;
declare de_current varchar(255);
declare de_step varchar(255);
declare de_len integer(11);
declare lingnum integer(11);
declare re varchar(255);
declare num varchar(255);
set de_id=0;
select id,current,step,len into de_id,de_current,de_step,de_len from ut_growth where prefix=de_prefix limit 1;
if de_id =0 then
insert into `ut_growth` (
    `current`,
    `step`,
    `prefix`,
    `len`
) 
values
    (
        0,
        1,
        de_prefix,
        6
    ) ;
    set de_current=1;
    set de_step=1;
    set de_len=6;
end if;
loop_label: loop
update 
    `ut_growth`
set
   `current` =current+de_step
   where  prefix=de_prefix;
set de_current=de_current+de_step;
set lingnum= de_len-length(de_current);
if lingnum>0
then
    set re=concat(de_prefix,repeat('0',lingnum),de_current);
    else 
    set re=concat(de_prefix,de_current);
    
end if;
select count(*) into num from zcdh_uni.`zcdh_ent_account` where account=re;
if num =0
then
	set col=re;
	leave loop_label;
end if;
end loop;
end$$
delimiter ;
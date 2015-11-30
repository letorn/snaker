delimiter $$

drop trigger `ent_after_update`$$

create
    trigger `ent_after_update` after update on `db_enterprise` 
    for each row begin
      update 
        `vi_enterprise` 
    set
        `contacter` = if(new.contacter is not null or new.contacter !='',new.contacter,contacter),
        `phone` =  if(new.phone is not null or new.phone !='',new.phone,phone),
        `fax` =  if(new.fax is not null or new.fax !='',new.fax,fax),
        `mobile` = if(new.mobile is not null or new.mobile !='',new.mobile,mobile),
        `email` =  if(new.mobile is not null or new.mobile !='',new.mobile,mobile),
        `qq` =  if(new.qq is not null or new.qq !='',new.qq,qq)
    where name = new.name ;
   
    end;
$$

delimiter ;
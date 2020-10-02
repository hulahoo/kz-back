alter table TSADV_SALARY add column TYPE_ varchar(50) ^
update TSADV_SALARY set TYPE_ = 'MONTHLYRATE' where TYPE_ is null ;
alter table TSADV_SALARY alter column TYPE_ set not null ;

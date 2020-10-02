alter table TSADV_RCG_FEEDBACK add column DIRECTION varchar(50) ^
update TSADV_RCG_FEEDBACK set DIRECTION = 'SEND' where DIRECTION is null ;
alter table TSADV_RCG_FEEDBACK alter column DIRECTION set not null ;

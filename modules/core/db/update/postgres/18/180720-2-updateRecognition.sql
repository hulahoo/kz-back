alter table TSADV_RECOGNITION add column COMMENT_EN varchar(2000) ^
update TSADV_RECOGNITION set COMMENT_EN = COMMENT_ where COMMENT_EN is null ;
alter table TSADV_RECOGNITION alter column COMMENT_EN set not null ;
alter table TSADV_RECOGNITION add column COMMENT_RU varchar(2000) ^
update TSADV_RECOGNITION set COMMENT_RU = COMMENT_ where COMMENT_RU is null ;
alter table TSADV_RECOGNITION alter column COMMENT_RU set not null ;

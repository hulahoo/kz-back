alter table TSADV_LEARNING_OBJECT add column CONTENT_TYPE varchar(50) ^
update TSADV_LEARNING_OBJECT set CONTENT_TYPE = 'URL' where CONTENT_TYPE is null ;
alter table TSADV_LEARNING_OBJECT alter column CONTENT_TYPE set not null ;
alter table TSADV_LEARNING_OBJECT add column FILE_ID uuid ;
alter table TSADV_LEARNING_OBJECT add column HTML text ;
alter table TSADV_LEARNING_OBJECT add column TEXT text ;

alter table TSADV_SCALE_LEVEL rename column level_name to level_name__u39391 ;
alter table TSADV_SCALE_LEVEL alter column level_name__u39391 drop not null ;
alter table TSADV_SCALE_LEVEL add column LEVEL_NAME_LANG1 varchar(1000) ^
--custom script
update TSADV_SCALE_LEVEL set LEVEL_NAME_LANG1 = COALESCE(level_name__u39391,'') ;
alter table TSADV_SCALE_LEVEL alter column LEVEL_NAME_LANG1 set not null ;
alter table TSADV_SCALE_LEVEL add column LEVEL_NAME_LANG2 varchar(1000) ;
alter table TSADV_SCALE_LEVEL add column LEVEL_NAME_LANG3 varchar(1000) ;
alter table TSADV_SCALE_LEVEL add column LEVEL_NAME_LANG4 varchar(1000) ;
alter table TSADV_SCALE_LEVEL add column LEVEL_NAME_LANG5 varchar(1000) ;

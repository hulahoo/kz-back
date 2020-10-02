alter table TSADV_SCALE_LEVEL rename column level_description to level_description__u09620 ;
alter table TSADV_SCALE_LEVEL add column LEVEL_DESCRIPTION_LANG1 varchar(4000) ;
alter table TSADV_SCALE_LEVEL add column LEVEL_DESCRIPTION_LANG2 varchar(4000) ;
alter table TSADV_SCALE_LEVEL add column LEVEL_DESCRIPTION_LANG3 varchar(4000) ;
alter table TSADV_SCALE_LEVEL add column LEVEL_DESCRIPTION_LANG4 varchar(4000) ;
alter table TSADV_SCALE_LEVEL add column LEVEL_DESCRIPTION_LANG5 varchar(4000) ;

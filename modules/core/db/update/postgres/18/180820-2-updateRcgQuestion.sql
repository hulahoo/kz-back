alter table TSADV_RCG_QUESTION rename column description to description__u50638 ;
alter table TSADV_RCG_QUESTION alter column description__u50638 drop not null ;
alter table TSADV_RCG_QUESTION rename column text to text__u76255 ;
alter table TSADV_RCG_QUESTION alter column text__u76255 drop not null ;
alter table TSADV_RCG_QUESTION add column TEXT_LANG1 varchar(1000) ^
update TSADV_RCG_QUESTION set TEXT_LANG1 = '' where TEXT_LANG1 is null ;
alter table TSADV_RCG_QUESTION alter column TEXT_LANG1 set not null ;
alter table TSADV_RCG_QUESTION add column TEXT_LANG2 varchar(1000) ;
alter table TSADV_RCG_QUESTION add column TEXT_LANG3 varchar(1000) ;
alter table TSADV_RCG_QUESTION add column TEXT_LANG4 varchar(1000) ;
alter table TSADV_RCG_QUESTION add column TEXT_LANG5 varchar(1000) ;
alter table TSADV_RCG_QUESTION add column DESCRIPTION_LANG1 varchar(1000) ^
update TSADV_RCG_QUESTION set DESCRIPTION_LANG1 = '' where DESCRIPTION_LANG1 is null ;
alter table TSADV_RCG_QUESTION alter column DESCRIPTION_LANG1 set not null ;
alter table TSADV_RCG_QUESTION add column DESCRIPTION_LANG2 varchar(1000) ;
alter table TSADV_RCG_QUESTION add column DESCRIPTION_LANG3 varchar(1000) ;
alter table TSADV_RCG_QUESTION add column DESCRIPTION_LANG4 varchar(1000) ;
alter table TSADV_RCG_QUESTION add column DESCRIPTION_LANG5 varchar(1000) ;

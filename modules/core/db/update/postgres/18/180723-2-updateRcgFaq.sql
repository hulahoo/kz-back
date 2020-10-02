alter table TSADV_RCG_FAQ rename column content to content__u30064 ;
alter table TSADV_RCG_FAQ alter column content__u30064 drop not null ;
alter table TSADV_RCG_FAQ rename column title to title__u79050 ;
alter table TSADV_RCG_FAQ alter column title__u79050 drop not null ;
alter table TSADV_RCG_FAQ add column TITLE_LANG1 varchar(1000) ^
update TSADV_RCG_FAQ set TITLE_LANG1 = '' where TITLE_LANG1 is null ;
alter table TSADV_RCG_FAQ alter column TITLE_LANG1 set not null ;
alter table TSADV_RCG_FAQ add column TITLE_LANG2 varchar(1000) ;
alter table TSADV_RCG_FAQ add column TITLE_LANG3 varchar(1000) ;
alter table TSADV_RCG_FAQ add column TITLE_LANG4 varchar(1000) ;
alter table TSADV_RCG_FAQ add column TITLE_LANG5 varchar(1000) ;
alter table TSADV_RCG_FAQ add column CONTENT_LANG1 text ;
alter table TSADV_RCG_FAQ add column CONTENT_LANG2 text ;
alter table TSADV_RCG_FAQ add column CONTENT_LANG3 text ;
alter table TSADV_RCG_FAQ add column CONTENT_LANG4 text ;
alter table TSADV_RCG_FAQ add column CONTENT_LANG5 text ;

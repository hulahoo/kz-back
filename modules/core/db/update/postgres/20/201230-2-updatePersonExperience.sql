alter table TSADV_PERSON_EXPERIENCE add column INDUSTRY_ID uuid ;
alter table TSADV_PERSON_EXPERIENCE add column PART_TIME boolean ;
alter table TSADV_PERSON_EXPERIENCE add column GROUP_EXPERIENCE boolean ;
alter table TSADV_PERSON_EXPERIENCE add column MINING_EXPERIENCE boolean ;
alter table TSADV_PERSON_EXPERIENCE add column START_DATE_HISTORY date ;
alter table TSADV_PERSON_EXPERIENCE add column LOCATION varchar(2000) ;
alter table TSADV_PERSON_EXPERIENCE add column END_DATE_HISTORY date ;

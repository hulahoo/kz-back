alter table TSADV_COMPETENCE rename column competence_name to competence_name__u12885 ;
alter table TSADV_COMPETENCE alter column competence_name__u12885 drop not null ;
alter table TSADV_COMPETENCE add column COMPETENCE_NAME_LANG1 varchar(1000) ^
--custom script
update TSADV_COMPETENCE set COMPETENCE_NAME_LANG1 = COALESCE(competence_name__u12885,'') ;
alter table TSADV_COMPETENCE alter column COMPETENCE_NAME_LANG1 set not null ;
alter table TSADV_COMPETENCE add column COMPETENCE_NAME_LANG2 varchar(1000) ;
alter table TSADV_COMPETENCE add column COMPETENCE_NAME_LANG3 varchar(1000) ;
alter table TSADV_COMPETENCE add column COMPETENCE_NAME_LANG4 varchar(1000) ;
alter table TSADV_COMPETENCE add column COMPETENCE_NAME_LANG5 varchar(1000) ;
alter table TSADV_COMPETENCE add column COMPETECE_TYPE_ID uuid ;

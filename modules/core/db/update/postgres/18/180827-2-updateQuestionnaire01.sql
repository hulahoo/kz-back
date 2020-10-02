alter table TSADV_QUESTIONNAIRE rename column questionnaire_name_kz to questionnaire_name_kz__u30599 ;
alter table TSADV_QUESTIONNAIRE rename column questionnaire_name_en to questionnaire_name_en__u55088 ;
alter table TSADV_QUESTIONNAIRE rename column questionnaire_name_ru to questionnaire_name_ru__u99581 ;
alter table TSADV_QUESTIONNAIRE alter column questionnaire_name_ru__u99581 drop not null ;
alter table TSADV_QUESTIONNAIRE rename column description_en to description_en__u40756 ;
alter table TSADV_QUESTIONNAIRE rename column description_kz to description_kz__u45201 ;
alter table TSADV_QUESTIONNAIRE rename column description_ru to description_ru__u99860 ;
alter table TSADV_QUESTIONNAIRE add column DESCRIPTION_LANG1 varchar(4000) ;
alter table TSADV_QUESTIONNAIRE add column DESCRIPTION_LANG3 varchar(4000) ;
alter table TSADV_QUESTIONNAIRE add column DESCRIPTION_LANG4 varchar(4000) ;
alter table TSADV_QUESTIONNAIRE add column DESCRIPTION_LANG5 varchar(4000) ;
alter table TSADV_QUESTIONNAIRE add column DESCRIPTION_LANG2 varchar(4000) ;
alter table TSADV_QUESTIONNAIRE add column QUESTIONNAIRE_NAME_LANG1 varchar(255) ^
update TSADV_QUESTIONNAIRE set QUESTIONNAIRE_NAME_LANG1 = '' where QUESTIONNAIRE_NAME_LANG1 is null ;
alter table TSADV_QUESTIONNAIRE alter column QUESTIONNAIRE_NAME_LANG1 set not null ;
alter table TSADV_QUESTIONNAIRE add column QUESTIONNAIRE_NAME_LANG2 varchar(255) ;
alter table TSADV_QUESTIONNAIRE add column QUESTIONNAIRE_NAME_LANG3 varchar(255) ;
alter table TSADV_QUESTIONNAIRE add column QUESTIONNAIRE_NAME_LANG4 varchar(255) ;
alter table TSADV_QUESTIONNAIRE add column QUESTIONNAIRE_NAME_LANG5 varchar(255) ;

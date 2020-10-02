alter table TSADV_QUESTIONNAIRE rename column questionnaire_name to questionnaire_name__u59593 ;
alter table TSADV_QUESTIONNAIRE alter column questionnaire_name__u59593 drop not null ;
alter table TSADV_QUESTIONNAIRE rename column description to description__u76192 ;
alter table TSADV_QUESTIONNAIRE add column DESCRIPTION_RU varchar(4000) ;
alter table TSADV_QUESTIONNAIRE add column DESCRIPTION_KZ varchar(4000) ;
alter table TSADV_QUESTIONNAIRE add column DESCRIPTION_EN varchar(4000) ;
alter table TSADV_QUESTIONNAIRE add column QUESTIONNAIRE_NAME_RU varchar(255) ^
update TSADV_QUESTIONNAIRE set QUESTIONNAIRE_NAME_RU = '' where QUESTIONNAIRE_NAME_RU is null ;
alter table TSADV_QUESTIONNAIRE alter column QUESTIONNAIRE_NAME_RU set not null ;
alter table TSADV_QUESTIONNAIRE add column QUESTIONNAIRE_NAME_EN varchar(255) ;
alter table TSADV_QUESTIONNAIRE add column QUESTIONNAIRE_NAME_KZ varchar(255) ;

alter table TSADV_QUESTIONNAIRE_QUESTION rename column question_type_id to question_type_id__u09680 ;
alter table TSADV_QUESTIONNAIRE_QUESTION alter column question_type_id__u09680 drop not null ;
drop index IDX_TSADV_QUESTIONNAIRE_QUESTION_QUESTION_TYPE ;
alter table TSADV_QUESTIONNAIRE_QUESTION drop constraint FK_TSADV_QUESTIONNAIRE_QUESTION_QUESTION_TYPE ;
alter table TSADV_QUESTIONNAIRE_QUESTION add column QUESTION_TYPE integer ^
update TSADV_QUESTIONNAIRE_QUESTION set QUESTION_TYPE = 1 where QUESTION_TYPE is null ;
alter table TSADV_QUESTIONNAIRE_QUESTION alter column QUESTION_TYPE set not null ;

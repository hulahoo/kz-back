alter table TSADV_QUESTIONNAIRE_QUESTION rename column question_text to question_text__u25983 ;
alter table TSADV_QUESTIONNAIRE_QUESTION alter column question_text__u25983 drop not null ;
alter table TSADV_QUESTIONNAIRE_QUESTION add column SECTION_ID uuid ;

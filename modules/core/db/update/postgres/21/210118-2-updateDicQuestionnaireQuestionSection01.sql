alter table TSADV_DIC_QUESTIONNAIRE_QUESTION_SECTION add constraint FK_TSADV_DIC_QUESTIONNAIRE_QUESTION_SECTION_COMPANY foreign key (COMPANY_ID) references BASE_DIC_COMPANY(ID);
create index IDX_TSADV_DIC_QUESTIONNAIRE_QUESTION_SECTION_COMPANY on TSADV_DIC_QUESTIONNAIRE_QUESTION_SECTION (COMPANY_ID);
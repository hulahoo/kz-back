alter table TSADV_PERSON_QUESTIONNAIRE add constraint FK_TSADV_PERSON_QUESTIONNAIRE_APPRAISE foreign key (APPRAISE_ID) references BASE_PERSON_GROUP(ID);
alter table TSADV_PERSON_QUESTIONNAIRE add constraint FK_TSADV_PERSON_QUESTIONNAIRE_APPRAISER foreign key (APPRAISER_ID) references BASE_PERSON_GROUP(ID);
alter table TSADV_PERSON_QUESTIONNAIRE add constraint FK_TSADV_PERSON_QUESTIONNAIRE_QUESTIONNAIRE foreign key (QUESTIONNAIRE_ID) references TSADV_QUESTIONNAIRE(ID);
create index IDX_TSADV_PERSON_QUESTIONNAIRE_APPRAISE on TSADV_PERSON_QUESTIONNAIRE (APPRAISE_ID);
create index IDX_TSADV_PERSON_QUESTIONNAIRE_APPRAISER on TSADV_PERSON_QUESTIONNAIRE (APPRAISER_ID);
create index IDX_TSADV_PERSON_QUESTIONNAIRE_QUESTIONNAIRE on TSADV_PERSON_QUESTIONNAIRE (QUESTIONNAIRE_ID);

alter table TSADV_PERSON_EDUCATION_REQUEST add constraint FK_TSADV_PERSON_EDUCATION_REQUEST_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
alter table TSADV_PERSON_EDUCATION_REQUEST add constraint FK_TSADV_PERSON_EDUCATION_REQUEST_EDUCATIONAL_INSTITUTION foreign key (EDUCATIONAL_INSTITUTION_ID) references TSADV_DIC_EDUCATIONAL_ESTABLISHMENT(ID);
alter table TSADV_PERSON_EDUCATION_REQUEST add constraint FK_TSADV_PERSON_EDUCATION_REQUEST_EDUCATION_TYPE foreign key (EDUCATION_TYPE_ID) references BASE_DIC_EDUCATION_TYPE(ID);
alter table TSADV_PERSON_EDUCATION_REQUEST add constraint FK_TSADV_PERSON_EDUCATION_REQUEST_DEGREE foreign key (DEGREE_ID) references TSADV_DIC_EDUCATION_DEGREE(ID);
alter table TSADV_PERSON_EDUCATION_REQUEST add constraint FK_TSADV_PERSON_EDUCATION_REQUEST_LEVEL foreign key (LEVEL_ID) references TSADV_DIC_EDUCATION_LEVEL(ID);
alter table TSADV_PERSON_EDUCATION_REQUEST add constraint FK_TSADV_PERSON_EDUCATION_REQUEST_FORM_STUDY foreign key (FORM_STUDY_ID) references TSADV_DIC_FORM_STUDY(ID);
alter table TSADV_PERSON_EDUCATION_REQUEST add constraint FK_TSADV_PERSON_EDUCATION_REQUEST_STATUS foreign key (STATUS_ID) references TSADV_DIC_REQUEST_STATUS(ID);
alter table TSADV_PERSON_EDUCATION_REQUEST add constraint FK_TSADV_PERSON_EDUCATION_REQUEST_FILE foreign key (FILE_ID) references SYS_FILE(ID);
create index IDX_TSADV_PERSON_EDUCATION_REQUEST_PERSON_GROUP on TSADV_PERSON_EDUCATION_REQUEST (PERSON_GROUP_ID);
create index IDX_TSADV_PERSON_EDUCATION_REQUEST_EDUCATIONAL_INSTITUTION on TSADV_PERSON_EDUCATION_REQUEST (EDUCATIONAL_INSTITUTION_ID);
create index IDX_TSADV_PERSON_EDUCATION_REQUEST_EDUCATION_TYPE on TSADV_PERSON_EDUCATION_REQUEST (EDUCATION_TYPE_ID);
create index IDX_TSADV_PERSON_EDUCATION_REQUEST_DEGREE on TSADV_PERSON_EDUCATION_REQUEST (DEGREE_ID);
create index IDX_TSADV_PERSON_EDUCATION_REQUEST_LEVEL on TSADV_PERSON_EDUCATION_REQUEST (LEVEL_ID);
create index IDX_TSADV_PERSON_EDUCATION_REQUEST_FORM_STUDY on TSADV_PERSON_EDUCATION_REQUEST (FORM_STUDY_ID);
create index IDX_TSADV_PERSON_EDUCATION_REQUEST_STATUS on TSADV_PERSON_EDUCATION_REQUEST (STATUS_ID);
create index IDX_TSADV_PERSON_EDUCATION_REQUEST_FILE on TSADV_PERSON_EDUCATION_REQUEST (FILE_ID);

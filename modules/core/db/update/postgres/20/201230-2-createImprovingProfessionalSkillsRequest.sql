alter table TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST add constraint FK_TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
alter table TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST add constraint FK_TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST_REQUEST_STATUS foreign key (REQUEST_STATUS_ID) references TSADV_DIC_REQUEST_STATUS(ID);
alter table TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST add constraint FK_TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST_FILE foreign key (FILE_ID) references SYS_FILE(ID);
create index IDX_TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST_PERSON_GROUP on TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST (PERSON_GROUP_ID);
create index IDX_TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST_REQUEST_STATUS on TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST (REQUEST_STATUS_ID);
create index IDX_TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST_FILE on TSADV_IMPROVING_PROFESSIONAL_SKILLS_REQUEST (FILE_ID);
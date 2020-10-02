alter table TSADV_ASSIGNMENT_REQUEST add constraint FK_TSADV_ASSIGNMENT_REQUEST_STATUS foreign key (STATUS_ID) references TSADV_DIC_REQUEST_STATUS(ID);
alter table TSADV_ASSIGNMENT_REQUEST add constraint FK_TSADV_ASSIGNMENT_REQUEST_POSITION_GROUP foreign key (POSITION_GROUP_ID) references BASE_POSITION_GROUP(ID);
alter table TSADV_ASSIGNMENT_REQUEST add constraint FK_TSADV_ASSIGNMENT_REQUEST_GRADE_GROUP foreign key (GRADE_GROUP_ID) references TSADV_GRADE_GROUP(ID);
alter table TSADV_ASSIGNMENT_REQUEST add constraint FK_TSADV_ASSIGNMENT_REQUEST_ORGANIZATION_GROUP foreign key (ORGANIZATION_GROUP_ID) references BASE_ORGANIZATION_GROUP(ID);
alter table TSADV_ASSIGNMENT_REQUEST add constraint FK_TSADV_ASSIGNMENT_REQUEST_JOB_GROUP foreign key (JOB_GROUP_ID) references TSADV_JOB_GROUP(ID);
alter table TSADV_ASSIGNMENT_REQUEST add constraint FK_TSADV_ASSIGNMENT_REQUEST_REASON foreign key (REASON_ID) references TSADV_DIC_SALARY_CHANGE_REASON(ID);
alter table TSADV_ASSIGNMENT_REQUEST add constraint FK_TSADV_ASSIGNMENT_REQUEST_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
create index IDX_TSADV_ASSIGNMENT_REQUEST_STATUS on TSADV_ASSIGNMENT_REQUEST (STATUS_ID);
create index IDX_TSADV_ASSIGNMENT_REQUEST_POSITION_GROUP on TSADV_ASSIGNMENT_REQUEST (POSITION_GROUP_ID);
create index IDX_TSADV_ASSIGNMENT_REQUEST_GRADE_GROUP on TSADV_ASSIGNMENT_REQUEST (GRADE_GROUP_ID);
create index IDX_TSADV_ASSIGNMENT_REQUEST_ORGANIZATION_GROUP on TSADV_ASSIGNMENT_REQUEST (ORGANIZATION_GROUP_ID);
create index IDX_TSADV_ASSIGNMENT_REQUEST_JOB_GROUP on TSADV_ASSIGNMENT_REQUEST (JOB_GROUP_ID);
create index IDX_TSADV_ASSIGNMENT_REQUEST_REASON on TSADV_ASSIGNMENT_REQUEST (REASON_ID);
create index IDX_TSADV_ASSIGNMENT_REQUEST_PERSON_GROUP on TSADV_ASSIGNMENT_REQUEST (PERSON_GROUP_ID);

alter table TSADV_VACATION_SCHEDULE add constraint FK_TSADV_VACATION_SCHEDULE_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
create index IDX_TSADV_VACATION_SCHEDULE_PERSON_GROUP on TSADV_VACATION_SCHEDULE (PERSON_GROUP_ID);
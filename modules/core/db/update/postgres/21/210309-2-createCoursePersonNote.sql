alter table TSADV_COURSE_PERSON_NOTE add constraint FK_TSADV_COURSE_PERSON_NOTE_COURSE foreign key (COURSE_ID) references TSADV_COURSE(ID);
alter table TSADV_COURSE_PERSON_NOTE add constraint FK_TSADV_COURSE_PERSON_NOTE_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
create index IDX_TSADV_COURSE_PERSON_NOTE_COURSE on TSADV_COURSE_PERSON_NOTE (COURSE_ID);
create index IDX_TSADV_COURSE_PERSON_NOTE_PERSON_GROUP on TSADV_COURSE_PERSON_NOTE (PERSON_GROUP_ID);

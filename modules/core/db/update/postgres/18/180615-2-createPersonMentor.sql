alter table TSADV_PERSON_MENTOR add constraint FK_TSADV_PERSON_MENTOR_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
alter table TSADV_PERSON_MENTOR add constraint FK_TSADV_PERSON_MENTOR_MENTOR foreign key (MENTOR_ID) references BASE_PERSON_GROUP(ID);
create index IDX_TSADV_PERSON_MENTOR_PERSON_GROUP on TSADV_PERSON_MENTOR (PERSON_GROUP_ID);
create index IDX_TSADV_PERSON_MENTOR_MENTOR on TSADV_PERSON_MENTOR (MENTOR_ID);

alter table TSADV_AWARDS add constraint FK_TSADV_AWARDS_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
create index IDX_TSADV_AWARDS_PERSON_GROUP on TSADV_AWARDS (PERSON_GROUP_ID);

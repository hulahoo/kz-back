alter table TSADV_CHILD_DESCRIPTION add constraint FK_TSADV_CHILD_DESCRIPTION_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
create index IDX_TSADV_CHILD_DESCRIPTION_PERSON_GROUP on TSADV_CHILD_DESCRIPTION (PERSON_GROUP_ID);

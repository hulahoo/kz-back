alter table SEC_USER add constraint FK_SEC_USER_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
create index IDX_SEC_USER_PERSON_GROUP on SEC_USER (PERSON_GROUP_ID);

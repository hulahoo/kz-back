alter table TSADV_PERSON_EXCEPTION add constraint FK_TSADV_PERSON_EXCEPTION_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
create unique index IDX_TSADV_PERSON_EXCEPTION_UK_PERSON_GROUP_ID on TSADV_PERSON_EXCEPTION (PERSON_GROUP_ID) where DELETE_TS is null ;
create index IDX_TSADV_PERSON_EXCEPTION_PERSON_GROUP on TSADV_PERSON_EXCEPTION (PERSON_GROUP_ID);

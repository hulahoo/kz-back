alter table TSADV_PERSON_LEARNING_CONTRACT add constraint FK_TSADV_PERSON_LEARNING_CONTRACT_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
create index IDX_TSADV_PERSON_LEARNING_CONTRACT_PERSON_GROUP on TSADV_PERSON_LEARNING_CONTRACT (PERSON_GROUP_ID);

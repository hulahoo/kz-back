alter table TSADV_BENEFICIARY add constraint FK_TSADV_BENEFICIARY_PERSON_GROUP foreign key (PERSON_GROUP_ID) references BASE_PERSON_GROUP(ID);
create index IDX_TSADV_BENEFICIARY_PERSON_GROUP on TSADV_BENEFICIARY (PERSON_GROUP_ID);
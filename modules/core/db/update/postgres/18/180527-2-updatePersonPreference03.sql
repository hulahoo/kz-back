alter table TSADV_PERSON_PREFERENCE add constraint FK_TSADV_PERSON_PREFERENCE_PREFERENCE_TYPE foreign key (PREFERENCE_TYPE_ID) references TSADV_DIC_PERSON_PREFERENCE_TYPE(ID);
create index IDX_TSADV_PERSON_PREFERENCE_PREFERENCE_TYPE on TSADV_PERSON_PREFERENCE (PREFERENCE_TYPE_ID);

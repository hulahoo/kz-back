alter table TSADV_BUDGET_HEADER add constraint FK_TSADV_BUDGET_HEADER_RESPONSIBLE_PERSON foreign key (RESPONSIBLE_PERSON_ID) references BASE_PERSON_GROUP(ID);
create index IDX_TSADV_BUDGET_HEADER_RESPONSIBLE_PERSON on TSADV_BUDGET_HEADER (RESPONSIBLE_PERSON_ID);

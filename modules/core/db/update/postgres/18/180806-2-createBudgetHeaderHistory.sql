alter table TSADV_BUDGET_HEADER_HISTORY add constraint FK_TSADV_BUDGET_HEADER_HISTORY_BUDGET_HEADER foreign key (BUDGET_HEADER_ID) references TSADV_BUDGET_HEADER(ID);
alter table TSADV_BUDGET_HEADER_HISTORY add constraint FK_TSADV_BUDGET_HEADER_HISTORY_CHANGE_PERSON foreign key (CHANGE_PERSON_ID) references BASE_PERSON_GROUP(ID);
create index IDX_TSADV_BUDGET_HEADER_HISTORY_BUDGET_HEADER on TSADV_BUDGET_HEADER_HISTORY (BUDGET_HEADER_ID);
create index IDX_TSADV_BUDGET_HEADER_HISTORY_CHANGE_PERSON on TSADV_BUDGET_HEADER_HISTORY (CHANGE_PERSON_ID);

alter table TSADV_PERSON_LEARNING_HISTORY add constraint FK_TSADV_PERSON_LEARNING_HISTORY_BUDGET_ITEM foreign key (BUDGET_ITEM_ID) references TSADV_DIC_BUDGET_ITEM(ID);
create index IDX_TSADV_PERSON_LEARNING_HISTORY_BUDGET_ITEM on TSADV_PERSON_LEARNING_HISTORY (BUDGET_ITEM_ID);

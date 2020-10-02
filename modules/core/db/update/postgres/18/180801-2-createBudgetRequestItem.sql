alter table TSADV_BUDGET_REQUEST_ITEM add constraint FK_TSADV_BUDGET_REQUEST_ITEM_BUDGET_REQUEST foreign key (BUDGET_REQUEST_ID) references TSADV_BUDGET_REQUEST(ID);
alter table TSADV_BUDGET_REQUEST_ITEM add constraint FK_TSADV_BUDGET_REQUEST_ITEM_BUDGET_ITEM foreign key (BUDGET_ITEM_ID) references TSADV_DIC_COST_TYPE(ID);
alter table TSADV_BUDGET_REQUEST_ITEM add constraint FK_TSADV_BUDGET_REQUEST_ITEM_CURRENCY foreign key (CURRENCY_ID) references BASE_DIC_CURRENCY(ID);
create index IDX_TSADV_BUDGET_REQUEST_ITEM_BUDGET_REQUEST on TSADV_BUDGET_REQUEST_ITEM (BUDGET_REQUEST_ID);
create index IDX_TSADV_BUDGET_REQUEST_ITEM_BUDGET_ITEM on TSADV_BUDGET_REQUEST_ITEM (BUDGET_ITEM_ID);
create index IDX_TSADV_BUDGET_REQUEST_ITEM_CURRENCY on TSADV_BUDGET_REQUEST_ITEM (CURRENCY_ID);

alter table TSADV_BUDGET_REQUEST_ITEM_DETAIL add constraint FK_TSADV_BUDGET_REQUEST_ITEM_DETAIL_BUDGET_REQUEST_ITEM foreign key (BUDGET_REQUEST_ITEM_ID) references TSADV_BUDGET_REQUEST_ITEM(ID);
create index IDX_TSADV_BUDGET_REQUEST_ITEM_DETAIL_BUDGET_REQUEST_ITEM on TSADV_BUDGET_REQUEST_ITEM_DETAIL (BUDGET_REQUEST_ITEM_ID);

alter table TSADV_GOODS_RECEIPT add constraint FK_TSADV_GOODS_RECEIPT_GOOD foreign key (GOOD_ID) references TSADV_GOODS(ID);
create index IDX_TSADV_GOODS_RECEIPT_GOOD on TSADV_GOODS_RECEIPT (GOOD_ID);

alter table TSADV_GOODS_WAREHOUSE add constraint FK_TSADV_GOODS_WAREHOUSE_GOODS foreign key (GOODS_ID) references TSADV_GOODS(ID);
create index IDX_TSADV_GOODS_WAREHOUSE_GOODS on TSADV_GOODS_WAREHOUSE (GOODS_ID);

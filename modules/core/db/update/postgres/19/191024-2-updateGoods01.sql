alter table TSADV_GOODS add constraint FK_TSADV_GOODS_RECOGNITION_PROVIDER foreign key (RECOGNITION_PROVIDER_ID) references TSADV_RECOGNITION_PROVIDER(ID);
create index IDX_TSADV_GOODS_RECOGNITION_PROVIDER on TSADV_GOODS (RECOGNITION_PROVIDER_ID);

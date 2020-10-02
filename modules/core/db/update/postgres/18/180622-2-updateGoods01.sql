alter table TSADV_GOODS add constraint FK_TSADV_GOODS_IMAGE foreign key (IMAGE_ID) references SYS_FILE(ID);
create index IDX_TSADV_GOODS_IMAGE on TSADV_GOODS (IMAGE_ID);

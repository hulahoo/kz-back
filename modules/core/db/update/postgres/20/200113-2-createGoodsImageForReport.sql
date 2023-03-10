alter table TSADV_GOODS_IMAGE_FOR_REPORT add constraint FK_TSADV_GOODS_IMAGE_FOR_REPORT_GOOD foreign key (GOOD_ID) references TSADV_GOODS(ID);
alter table TSADV_GOODS_IMAGE_FOR_REPORT add constraint FK_TSADV_GOODS_IMAGE_FOR_REPORT_IMAGE foreign key (IMAGE_ID) references SYS_FILE(ID);
create index IDX_TSADV_GOODS_IMAGE_FOR_REPORT_GOOD on TSADV_GOODS_IMAGE_FOR_REPORT (GOOD_ID);
create index IDX_TSADV_GOODS_IMAGE_FOR_REPORT_IMAGE on TSADV_GOODS_IMAGE_FOR_REPORT (IMAGE_ID);

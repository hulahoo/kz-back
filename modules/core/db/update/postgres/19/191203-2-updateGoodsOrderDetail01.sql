alter table TSADV_GOODS_ORDER_DETAIL add constraint FK_TSADV_GOODS_ORDER_DETAIL_QR_CODE_IMG foreign key (QR_CODE_IMG_ID) references SYS_FILE(ID);

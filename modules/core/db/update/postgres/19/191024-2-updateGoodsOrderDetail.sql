alter table TSADV_GOODS_ORDER_DETAIL add column VOUCHER_USED boolean ^
update TSADV_GOODS_ORDER_DETAIL set VOUCHER_USED = false where VOUCHER_USED is null ;
alter table TSADV_GOODS_ORDER_DETAIL alter column VOUCHER_USED set not null ;
alter table TSADV_GOODS_ORDER_DETAIL add column RECOGNITION_PROVIDER_ID uuid ;
alter table TSADV_GOODS_ORDER_DETAIL add column QR_CODE varchar(255) ;

alter table TSADV_GOODS_RECEIPT rename column good_id to good_id__u78747 ;
alter table TSADV_GOODS_RECEIPT alter column good_id__u78747 drop not null ;
drop index IDX_TSADV_GOODS_RECEIPT_GOOD ;
alter table TSADV_GOODS_RECEIPT drop constraint FK_TSADV_GOODS_RECEIPT_GOOD ;
-- alter table TSADV_GOODS_RECEIPT add column GOODS_ID uuid ^
-- update TSADV_GOODS_RECEIPT set GOODS_ID = <default_value> ;
-- alter table TSADV_GOODS_RECEIPT alter column GOODS_ID set not null ;
alter table TSADV_GOODS_RECEIPT add column GOODS_ID uuid not null ;
alter table TSADV_GOODS_RECEIPT alter column QUANTITY set data type bigint ;

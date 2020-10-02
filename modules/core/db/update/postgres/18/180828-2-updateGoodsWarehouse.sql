alter table TSADV_GOODS_WAREHOUSE rename column good_id to good_id__u55584 ;
alter table TSADV_GOODS_WAREHOUSE alter column good_id__u55584 drop not null ;
drop index IDX_TSADV_GOODS_WAREHOUSE_GOOD ;
alter table TSADV_GOODS_WAREHOUSE drop constraint FK_TSADV_GOODS_WAREHOUSE_GOOD ;
-- alter table TSADV_GOODS_WAREHOUSE add column GOODS_ID uuid ^
-- update TSADV_GOODS_WAREHOUSE set GOODS_ID = <default_value> ;
-- alter table TSADV_GOODS_WAREHOUSE alter column GOODS_ID set not null ;
alter table TSADV_GOODS_WAREHOUSE add column GOODS_ID uuid not null ;
alter table TSADV_GOODS_WAREHOUSE alter column QUANTITY set data type bigint ;

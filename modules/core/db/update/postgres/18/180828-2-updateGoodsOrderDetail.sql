alter table TSADV_GOODS_ORDER_DETAIL add column COMMENT_ varchar(2000) ;
alter table TSADV_GOODS_ORDER_DETAIL add column EXCLUDED boolean ^
update TSADV_GOODS_ORDER_DETAIL set EXCLUDED = false where EXCLUDED is null ;
alter table TSADV_GOODS_ORDER_DETAIL alter column EXCLUDED set not null ;

alter table TSADV_GOODS_IMAGE add column PRIMARY_ boolean ^
update TSADV_GOODS_IMAGE set PRIMARY_ = false where PRIMARY_ is null ;
alter table TSADV_GOODS_IMAGE alter column PRIMARY_ set not null ;

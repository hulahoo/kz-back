-- alter table TSADV_BUDGET_REQUEST_ITEM_DETAIL add column BUDGET_REQUEST_ITEM_ID uuid ^
-- update TSADV_BUDGET_REQUEST_ITEM_DETAIL set BUDGET_REQUEST_ITEM_ID = <default_value> ;
-- alter table TSADV_BUDGET_REQUEST_ITEM_DETAIL alter column BUDGET_REQUEST_ITEM_ID set not null ;
alter table TSADV_BUDGET_REQUEST_ITEM_DETAIL add column BUDGET_REQUEST_ITEM_ID uuid not null ;
alter table TSADV_BUDGET_REQUEST_ITEM_DETAIL add column MONTH_ date ^
update TSADV_BUDGET_REQUEST_ITEM_DETAIL set MONTH_ = current_date where MONTH_ is null ;
alter table TSADV_BUDGET_REQUEST_ITEM_DETAIL alter column MONTH_ set not null ;
alter table TSADV_BUDGET_REQUEST_ITEM_DETAIL alter column BUDGET_REQUEST_DETAIL_ID drop not null ;
alter table TSADV_BUDGET_REQUEST_ITEM_DETAIL alter column BUDGET_ITEM_ID drop not null ;
alter table TSADV_BUDGET_REQUEST_ITEM_DETAIL alter column CURRENCY_ID drop not null ;

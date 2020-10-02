alter table TSADV_BUDGET_REQUEST_ITEM add column NAME varchar(255) ^
update TSADV_BUDGET_REQUEST_ITEM set NAME = '' where NAME is null ;
alter table TSADV_BUDGET_REQUEST_ITEM alter column NAME set not null ;

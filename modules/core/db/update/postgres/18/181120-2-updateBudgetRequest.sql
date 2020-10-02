-- alter table TSADV_BUDGET_REQUEST add column BUDGET_HEADER_ID uuid ^
-- update TSADV_BUDGET_REQUEST set BUDGET_HEADER_ID = <default_value> ;
-- alter table TSADV_BUDGET_REQUEST alter column BUDGET_HEADER_ID set not null ;
alter table TSADV_BUDGET_REQUEST add column LEGACY_ID varchar(255) ;

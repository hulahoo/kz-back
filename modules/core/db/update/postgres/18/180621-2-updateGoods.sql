-- alter table TSADV_GOODS add column CATEGORY_ID uuid ^
-- update TSADV_GOODS set CATEGORY_ID = <default_value> ;
-- alter table TSADV_GOODS alter column CATEGORY_ID set not null ;
alter table TSADV_GOODS add column CATEGORY_ID uuid not null ;

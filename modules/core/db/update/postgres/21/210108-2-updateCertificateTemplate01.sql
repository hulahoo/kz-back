-- alter table TSADV_CERTIFICATE_TEMPLATE add column CERTIFICATE_TYPE_ID uuid ^
-- update TSADV_CERTIFICATE_TEMPLATE set CERTIFICATE_TYPE_ID = <default_value> ;
-- alter table TSADV_CERTIFICATE_TEMPLATE alter column CERTIFICATE_TYPE_ID set not null ;
alter table TSADV_CERTIFICATE_TEMPLATE add column if not exists CERTIFICATE_TYPE_ID uuid;

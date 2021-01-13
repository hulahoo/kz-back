alter table TSADV_CERTIFICATE_TEMPLATE rename column language_id to language_id__u12183 ;
alter table TSADV_CERTIFICATE_TEMPLATE alter column language_id__u12183 drop not null ;
alter table TSADV_CERTIFICATE_TEMPLATE drop constraint IF EXISTS FK_TSADV_CERTIFICATE_TEMPLATE_LANGUAGE ;
drop index IDX_TSADV_CERTIFICATE_TEMPLATE_LANGUAGE ;
-- alter table TSADV_CERTIFICATE_TEMPLATE add column LANGUAGE_ID uuid ^
-- update TSADV_CERTIFICATE_TEMPLATE set LANGUAGE_ID = <default_value> ;
-- alter table TSADV_CERTIFICATE_TEMPLATE alter column language_id set not null ;
alter table TSADV_CERTIFICATE_TEMPLATE add column if not exists LANGUAGE_ID uuid not null ;
-- update TSADV_CERTIFICATE_TEMPLATE set CERTIFICATE_TYPE_ID = <default_value> where CERTIFICATE_TYPE_ID is null ;
alter table TSADV_CERTIFICATE_TEMPLATE alter column CERTIFICATE_TYPE_ID set not null ;

alter table TSADV_ORG_STRUCTURE_REQUEST rename column company_id to company_id__u19084 ;
alter table TSADV_ORG_STRUCTURE_REQUEST alter column company_id__u19084 drop not null ;
alter table TSADV_ORG_STRUCTURE_REQUEST drop constraint FK_TSADV_ORG_STRUCTURE_REQUEST_COMPANY ;
drop index IDX_TSADV_ORG_STRUCTURE_REQUEST_COMPANY ;
-- alter table TSADV_ORG_STRUCTURE_REQUEST add column COMPANY_ID uuid ^
-- update TSADV_ORG_STRUCTURE_REQUEST set COMPANY_ID = <default_value> ;
-- alter table TSADV_ORG_STRUCTURE_REQUEST alter column company_id set not null ;
alter table TSADV_ORG_STRUCTURE_REQUEST add column COMPANY_ID uuid not null ;

alter table BASE_ORGANIZATION rename column company_id to company_id__u07232 ;
alter table BASE_ORGANIZATION drop constraint FK_BASE_ORGANIZATION_COMPANY ;
drop index IDX_BASE_ORGANIZATION_COMPANY ;
alter table BASE_ORGANIZATION add column COMPANY_ID uuid ;

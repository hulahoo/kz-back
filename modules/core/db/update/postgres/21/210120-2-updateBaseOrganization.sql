alter table BASE_ORGANIZATION rename column company_id to company_id__u44004 ;
alter table BASE_ORGANIZATION drop constraint FK_BASE_ORGANIZATION_COMPANY ;
drop index IDX_BASE_ORGANIZATION_COMPANY ;

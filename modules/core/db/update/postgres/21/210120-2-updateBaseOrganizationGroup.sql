alter table BASE_ORGANIZATION_GROUP rename column company_id to company_id__u38697 ;
alter table BASE_ORGANIZATION_GROUP drop constraint FK_BASE_ORGANIZATION_GROUP_COMPANY ;
drop index IDX_BASE_ORGANIZATION_GROUP_COMPANY ;
alter table BASE_ORGANIZATION_GROUP add column COMPANY_ID uuid ;

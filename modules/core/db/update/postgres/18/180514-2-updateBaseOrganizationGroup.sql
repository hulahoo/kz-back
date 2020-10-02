alter table BASE_ORGANIZATION_GROUP rename column org_analytics_id to org_analytics_id__u03705 ;
alter table BASE_ORGANIZATION_GROUP drop constraint FK_BASE_ORGANIZATION_GROUP_ORG_ANALYTICS ;
alter table BASE_ORGANIZATION_GROUP add column ANALYTICS_ID uuid ;

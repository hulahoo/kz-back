alter table BASE_ASSIGNMENT_GROUP rename column org_analytics_id to org_analytics_id__u51890 ;
alter table BASE_ASSIGNMENT_GROUP drop constraint FK_BASE_ASSIGNMENT_GROUP_ORG_ANALYTICS ;
alter table BASE_ASSIGNMENT_GROUP add column ANALYTICS_ID uuid ;

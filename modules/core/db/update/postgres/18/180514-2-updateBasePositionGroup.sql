alter table BASE_POSITION_GROUP rename column org_analytics_id to org_analytics_id__u87545 ;
alter table BASE_POSITION_GROUP drop constraint FK_BASE_POSITION_GROUP_ORG_ANALYTICS ;
alter table BASE_POSITION_GROUP add column ANALYTICS_ID uuid ;

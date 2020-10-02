alter table BASE_POSITION_GROUP rename column admin_approve_id to admin_approve_id__u28597 ;
drop index IDX_BASE_POSITION_GROUP_ADMIN_APPROVE ;
alter table BASE_POSITION_GROUP drop constraint FK_BASE_POSITION_GROUP_ADMIN_APPROVE ;
alter table BASE_POSITION_GROUP add column ADMIN_APPROVE_ID uuid ;

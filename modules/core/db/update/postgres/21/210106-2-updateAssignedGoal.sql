alter table TSADV_ASSIGNED_GOAL rename column parent_id to parent_id__u21120 ;
alter table TSADV_ASSIGNED_GOAL add column PARENT_ASSIGNED_GOAL_ID uuid ;

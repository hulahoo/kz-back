alter table TSADV_ASSIGNED_GOAL rename column performance_plan_id to performance_plan_id__u55990 ;
alter table TSADV_ASSIGNED_GOAL drop constraint FK_TSADV_ASSIGNED_GOAL_PERFORMANCE_PLAN ;
drop index IDX_TSADV_ASSIGNED_GOAL_PERFORMANCE_PLAN ;
drop index IDX_TSADV_ASSIGNED_GOAL_UNQ ;
alter table TSADV_ASSIGNED_GOAL add column ASSIGNED_PERFORMANCE_PLAN_ID uuid ;

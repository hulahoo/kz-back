alter table TSADV_ASSIGNED_GOAL add constraint FK_TSADV_ASSIGNED_GOAL_ASSIGNED_PERFORMANCE_PLAN foreign key (ASSIGNED_PERFORMANCE_PLAN_ID) references TSADV_ASSIGNED_PERFORMANCE_PLAN(ID);
create index IDX_TSADV_ASSIGNED_GOAL_ASSIGNED_PERFORMANCE_PLAN on TSADV_ASSIGNED_GOAL (ASSIGNED_PERFORMANCE_PLAN_ID);

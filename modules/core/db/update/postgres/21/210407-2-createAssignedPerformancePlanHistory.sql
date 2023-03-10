alter table TSADV_ASSIGNED_PERFORMANCE_PLAN_HISTORY add constraint FK_TSADV_ASSIGNED_PERFORMANCE_PLAN_HISTORY_STAGE foreign key (STAGE_ID) references TSADV_DIC_PERFORMANCE_STAGE(ID);
alter table TSADV_ASSIGNED_PERFORMANCE_PLAN_HISTORY add constraint FK_TSADV_ASSIGNED_PERFORMANCE_PLAN_HISTORY_STATUS foreign key (STATUS_ID) references TSADV_DIC_REQUEST_STATUS(ID);
alter table TSADV_ASSIGNED_PERFORMANCE_PLAN_HISTORY add constraint FK_TSADV_ASSIGNEPERFORMPLANHISTORY_ASSIGNED_PERFORMANCE_PLAN foreign key (ASSIGNED_PERFORMANCE_PLAN_ID) references TSADV_ASSIGNED_PERFORMANCE_PLAN(ID);
create index IDX_TSADV_ASSIGNED_PERFORMANCE_PLAN_HISTORY_STAGE on TSADV_ASSIGNED_PERFORMANCE_PLAN_HISTORY (STAGE_ID);
create index IDX_TSADV_ASSIGNED_PERFORMANCE_PLAN_HISTORY_STATUS on TSADV_ASSIGNED_PERFORMANCE_PLAN_HISTORY (STATUS_ID);
create index IDX_TSADV_ASSIGNEPERFORMPLANHISTORY_ASSIGNED_PERFORMANCE_PLAN on TSADV_ASSIGNED_PERFORMANCE_PLAN_HISTORY (ASSIGNED_PERFORMANCE_PLAN_ID);

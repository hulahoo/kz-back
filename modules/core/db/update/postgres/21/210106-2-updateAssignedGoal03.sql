alter table TSADV_ASSIGNED_GOAL add constraint FK_TSADV_ASSIGNED_GOAL_GOAL_LIBRARY foreign key (GOAL_LIBRARY_ID) references TSADV_GOAL_LIBRARY(ID);
create index IDX_TSADV_ASSIGNED_GOAL_GOAL_LIBRARY on TSADV_ASSIGNED_GOAL (GOAL_LIBRARY_ID);

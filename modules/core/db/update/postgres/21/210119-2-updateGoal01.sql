alter table TSADV_GOAL add constraint FK_TSADV_GOAL_PARENT_GOAL foreign key (PARENT_GOAL_ID) references TSADV_GOAL(ID);
create index IDX_TSADV_GOAL_PARENT_GOAL on TSADV_GOAL (PARENT_GOAL_ID);

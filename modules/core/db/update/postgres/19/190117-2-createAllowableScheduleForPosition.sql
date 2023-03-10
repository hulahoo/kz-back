alter table TSADV_ALLOWABLE_SCHEDULE_FOR_POSITION add constraint FK_TSADV_ALLOWABLE_SCHEDULE_FOR_POSITION_POSITION_GROUP foreign key (POSITION_GROUP_ID) references BASE_POSITION_GROUP(ID);
alter table TSADV_ALLOWABLE_SCHEDULE_FOR_POSITION add constraint FK_TSADV_ALLOWABLE_SCHEDULE_FOR_POSITION_SCHEDULE foreign key (SCHEDULE_ID) references TSADV_STANDARD_SCHEDULE(ID);
create index IDX_TSADV_ALLOWABLE_SCHEDULE_FOR_POSITION_POSITION_GROUP on TSADV_ALLOWABLE_SCHEDULE_FOR_POSITION (POSITION_GROUP_ID);
create index IDX_TSADV_ALLOWABLE_SCHEDULE_FOR_POSITION_SCHEDULE on TSADV_ALLOWABLE_SCHEDULE_FOR_POSITION (SCHEDULE_ID);

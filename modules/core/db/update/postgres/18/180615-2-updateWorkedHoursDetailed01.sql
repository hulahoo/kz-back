alter table TSADV_WORKED_HOURS_DETAILED add constraint FK_TSADV_WORKED_HOURS_DETAILED_TIMECARD_HEADER foreign key (TIMECARD_HEADER_ID) references TSADV_TIMECARD_HEADER(ID);
create index IDX_TSADV_WORKED_HOURS_DETAILED_TIMECARD_HEADER on TSADV_WORKED_HOURS_DETAILED (TIMECARD_HEADER_ID);

alter table TSADV_SCHEDULE_OFFSETS_REQUEST add constraint FK_TSADV_SCHEDULE_OFFSETS_REQUEST_EARNING_POLICY foreign key (EARNING_POLICY_ID) references TSADV_DIC_EARNING_POLICY(ID);
create index IDX_TSADV_SCHEDULE_OFFSETS_REQUEST_EARNING_POLICY on TSADV_SCHEDULE_OFFSETS_REQUEST (EARNING_POLICY_ID);
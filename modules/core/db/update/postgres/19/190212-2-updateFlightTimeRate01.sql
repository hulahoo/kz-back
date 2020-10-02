alter table TSADV_FLIGHT_TIME_RATE add constraint FK_TSADV_FLIGHT_TIME_RATE_DIC_RATE_TYPE foreign key (DIC_RATE_TYPE_ID) references TSADV_DIC_RATE_TYPE(ID);
create index IDX_TSADV_FLIGHT_TIME_RATE_DIC_RATE_TYPE on TSADV_FLIGHT_TIME_RATE (DIC_RATE_TYPE_ID);

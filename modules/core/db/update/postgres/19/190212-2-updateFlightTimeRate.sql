alter table TSADV_FLIGHT_TIME_RATE rename column rate_type to rate_type__u84133 ;
alter table TSADV_FLIGHT_TIME_RATE add column DIC_RATE_TYPE_ID uuid ;

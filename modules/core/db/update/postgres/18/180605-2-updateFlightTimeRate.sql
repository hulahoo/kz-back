alter table TSADV_FLIGHT_TIME_RATE rename column hours_to to hours_to__u27319 ;
alter table TSADV_FLIGHT_TIME_RATE rename column hours_from to hours_from__u53763 ;
alter table TSADV_FLIGHT_TIME_RATE add column HOURS_FROM double precision ;
alter table TSADV_FLIGHT_TIME_RATE add column HOURS_TO double precision ;

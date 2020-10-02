alter table TSADV_FLIGHT_TIME_RATE rename column hours_to to hours_to__u71357 ;
alter table TSADV_FLIGHT_TIME_RATE rename column hours_from to hours_from__u50159 ;
alter table TSADV_FLIGHT_TIME_RATE add column HOURS_FROM time ;
alter table TSADV_FLIGHT_TIME_RATE add column HOURS_TO time ;

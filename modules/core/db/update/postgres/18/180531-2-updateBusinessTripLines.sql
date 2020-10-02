alter table TSADV_BUSINESS_TRIP_LINES rename column city_id to city_id__u71253 ;
alter table TSADV_BUSINESS_TRIP_LINES alter column city_id__u71253 drop not null ;
drop index IDX_TSADV_BUSINESS_TRIP_LINES_CITY ;
alter table TSADV_BUSINESS_TRIP_LINES drop constraint FK_TSADV_BUSINESS_TRIP_LINES_CITY ;
-- alter table TSADV_BUSINESS_TRIP_LINES add column CITY_TO_ID uuid ^
-- update TSADV_BUSINESS_TRIP_LINES set CITY_TO_ID = <default_value> ;
-- alter table TSADV_BUSINESS_TRIP_LINES alter column CITY_TO_ID set not null ;
alter table TSADV_BUSINESS_TRIP_LINES add column CITY_TO_ID uuid not null ;
alter table TSADV_BUSINESS_TRIP_LINES add column CITY_FROM_ID uuid ;
alter table TSADV_BUSINESS_TRIP_LINES add column NUMBER_ varchar(255) ;

alter table TSADV_DIC_BUSINESS_TRIP_TYPE add column WORKING_DAY boolean ^
update TSADV_DIC_BUSINESS_TRIP_TYPE set WORKING_DAY = false where WORKING_DAY is null ;
alter table TSADV_DIC_BUSINESS_TRIP_TYPE alter column WORKING_DAY set not null ;

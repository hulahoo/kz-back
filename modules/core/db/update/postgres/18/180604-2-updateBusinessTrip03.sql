alter table TSADV_BUSINESS_TRIP add constraint FK_TSADV_BUSINESS_TRIP_PARENT_BUSINESS_TRIP foreign key (PARENT_BUSINESS_TRIP_ID) references TSADV_BUSINESS_TRIP(ID);
create index IDX_TSADV_BUSINESS_TRIP_PARENT_BUSINESS_TRIP on TSADV_BUSINESS_TRIP (PARENT_BUSINESS_TRIP_ID);

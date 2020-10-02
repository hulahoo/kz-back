alter table TSADV_BUSINESS_TRIP rename column parent_order_id to parent_order_id__u45413 ;
drop index IDX_TSADV_BUSINESS_TRIP_PARENT_ORDER ;
alter table TSADV_BUSINESS_TRIP drop constraint FK_TSADV_BUSINESS_TRIP_PARENT_ORDER ;
alter table TSADV_BUSINESS_TRIP add column PARENT_BUSINESS_TRIP_ID uuid ;

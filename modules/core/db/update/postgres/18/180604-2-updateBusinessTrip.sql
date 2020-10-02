alter table TSADV_BUSINESS_TRIP add column CANCEL_ORDER_NUMBER varchar(255) ;
alter table TSADV_BUSINESS_TRIP add column CANCEL_ORDER_DATE date ;
alter table TSADV_BUSINESS_TRIP add column PARENT_ORDER_ID uuid ;

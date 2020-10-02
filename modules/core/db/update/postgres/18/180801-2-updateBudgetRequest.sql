alter table TSADV_BUDGET_REQUEST add column BUDGET_HEADER_ID uuid ;
alter table TSADV_BUDGET_REQUEST add column REASON varchar(255) ;
alter table TSADV_BUDGET_REQUEST add column CITY_ID uuid ;
alter table TSADV_BUDGET_REQUEST add column DAY_ integer ;
alter table TSADV_BUDGET_REQUEST add column HOUR_ integer ;
alter table TSADV_BUDGET_REQUEST add column BUSINESS_TRIP_EMPLOYEE integer ;
alter table TSADV_BUDGET_REQUEST add column BUDGET_ITEM_ID uuid ;

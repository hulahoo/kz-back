alter table TSADV_ADDRESS rename column city to city__u49698 ;
alter table TSADV_ADDRESS add column CITY_ID uuid ;
alter table TSADV_ADDRESS add column CITY_NAME varchar(255) ;
alter table TSADV_ADDRESS add column LANGUAGE_ID uuid ;

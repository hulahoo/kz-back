alter table TSADV_POSITION_CHANGE_REQUEST rename column request_number to request_number__u47236 ;
alter table TSADV_POSITION_CHANGE_REQUEST alter column request_number__u47236 drop not null ;
alter table TSADV_POSITION_CHANGE_REQUEST add column REQUEST_NUMBER bigint ^
update TSADV_POSITION_CHANGE_REQUEST set REQUEST_NUMBER = 0 where REQUEST_NUMBER is null ;
alter table TSADV_POSITION_CHANGE_REQUEST alter column REQUEST_NUMBER set not null ;

delete from tsadv_absence;
delete from TSADV_ABSENCE_REQUEST;
alter table TSADV_ABSENCE_REQUEST alter column REQUEST_NUMBER set not null ;
alter table TSADV_ABSENCE_REQUEST alter column STATUS_ID set not null ;
alter table TSADV_ABSENCE_REQUEST alter column REQUEST_DATE set not null ;

alter table TSADV_ABSENCE_RVD_REQUEST add column SHIFT_CODE varchar(255) ;
alter table TSADV_ABSENCE_RVD_REQUEST add column SHIFT_ID uuid ;
alter table TSADV_ABSENCE_RVD_REQUEST add column OVERRIDE_ALL_HOURS_BY_DAY varchar(50) ;

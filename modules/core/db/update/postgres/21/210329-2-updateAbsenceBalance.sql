alter table TSADV_ABSENCE_BALANCE rename column extra_days_left to extra_days_left__u68486 ;
alter table TSADV_ABSENCE_BALANCE rename column days_left to days_left__u66588 ;
alter table TSADV_ABSENCE_BALANCE add column ECOLOGICAL_DUE_DAYS integer ;
alter table TSADV_ABSENCE_BALANCE add column DISABILITY_DUE_DAYS integer ;
alter table TSADV_ABSENCE_BALANCE add column DISABILITY_DAYS_LEFT double precision ;
alter table TSADV_ABSENCE_BALANCE add column ECOLOGICAL_DAYS_LEFT double precision ;
alter table TSADV_ABSENCE_BALANCE add column DAYS_LEFT double precision ;
alter table TSADV_ABSENCE_BALANCE add column EXTRA_DAYS_LEFT double precision ;

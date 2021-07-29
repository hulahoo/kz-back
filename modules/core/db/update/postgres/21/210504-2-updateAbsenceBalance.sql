alter table TSADV_ABSENCE_BALANCE rename column disability_due_days to disability_due_days__u59269 ;
alter table TSADV_ABSENCE_BALANCE rename column ecological_due_days to ecological_due_days__u73127 ;
alter table TSADV_ABSENCE_BALANCE rename column add_balance_days_aims to add_balance_days_aims__u17592 ;
alter table TSADV_ABSENCE_BALANCE rename column long_absence_days to long_absence_days__u32493 ;
alter table TSADV_ABSENCE_BALANCE rename column extra_days_spent to extra_days_spent__u70125 ;
alter table TSADV_ABSENCE_BALANCE rename column days_spent to days_spent__u77612 ;
alter table TSADV_ABSENCE_BALANCE rename column additional_balance_days to additional_balance_days__u50712 ;
alter table TSADV_ABSENCE_BALANCE alter column additional_balance_days__u50712 drop not null ;
alter table TSADV_ABSENCE_BALANCE rename column balance_days to balance_days__u71219 ;
alter table TSADV_ABSENCE_BALANCE alter column balance_days__u71219 drop not null ;
alter table TSADV_ABSENCE_BALANCE rename column overall_balance_days to overall_balance_days__u41787 ;
alter table TSADV_ABSENCE_BALANCE add column OVERALL_BALANCE_DAYS double precision ;
alter table TSADV_ABSENCE_BALANCE add column BALANCE_DAYS double precision ^
update TSADV_ABSENCE_BALANCE set BALANCE_DAYS = 0 where BALANCE_DAYS is null ;
alter table TSADV_ABSENCE_BALANCE alter column BALANCE_DAYS set not null ;
alter table TSADV_ABSENCE_BALANCE add column ADDITIONAL_BALANCE_DAYS double precision ^
update TSADV_ABSENCE_BALANCE set ADDITIONAL_BALANCE_DAYS = 0 where ADDITIONAL_BALANCE_DAYS is null ;
alter table TSADV_ABSENCE_BALANCE alter column ADDITIONAL_BALANCE_DAYS set not null ;
alter table TSADV_ABSENCE_BALANCE add column DAYS_SPENT double precision ;
alter table TSADV_ABSENCE_BALANCE add column EXTRA_DAYS_SPENT double precision ;
alter table TSADV_ABSENCE_BALANCE add column LONG_ABSENCE_DAYS double precision ;
alter table TSADV_ABSENCE_BALANCE add column ADD_BALANCE_DAYS_AIMS double precision ;
alter table TSADV_ABSENCE_BALANCE add column ECOLOGICAL_DUE_DAYS double precision ;
alter table TSADV_ABSENCE_BALANCE add column DISABILITY_DUE_DAYS double precision ;
